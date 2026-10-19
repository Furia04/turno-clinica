package com.ucc.turnoclinica;

import com.ucc.turnoclinica.dto.*;
import com.ucc.turnoclinica.entity.EstadoTurno;
import com.ucc.turnoclinica.exception.PacienteNotFoundException;
import com.ucc.turnoclinica.exception.ProfesionalNotFoundException;
import com.ucc.turnoclinica.exception.ReglaDeNegocioException;
import com.ucc.turnoclinica.exception.TurnoNotFoundException;
import com.ucc.turnoclinica.service.PacienteService;
import com.ucc.turnoclinica.service.ProfesionalService;
import com.ucc.turnoclinica.service.TurnoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class TurnoClinicaApplicationTests {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private ProfesionalService profesionalService;

    @Autowired
    private TurnoService turnoService;

    @Test
    void contextLoads() {
        assertNotNull(pacienteService);
        assertNotNull(profesionalService);
        assertNotNull(turnoService);
    }

    @Test
    @DisplayName("Caso 1: Crear un registro válido (201 y DTO con ID generado)")
    void testCrearRegistrosValidos() {
        PacienteRequestDTO pacienteReq = new PacienteRequestDTO(
                "Esteban",
                "Quito",
                "39123888",
                "+54 351 1234567",
                "esteban.quito@ucc.edu.ar",
                "OSDE"
        );
        PacienteResponseDTO pacienteRes = pacienteService.crear(pacienteReq);
        assertNotNull(pacienteRes.id());
        assertEquals("Esteban", pacienteRes.nombre());
        assertEquals("39123888", pacienteRes.dni());

        ProfesionalRequestDTO profesionalReq = new ProfesionalRequestDTO(
                "Laura",
                "Benitez",
                "MP-8833",
                "Dermatología"
        );
        ProfesionalResponseDTO profesionalRes = profesionalService.crear(profesionalReq);
        assertNotNull(profesionalRes.id());
        assertEquals("MP-8833", profesionalRes.matricula());
    }

    @Test
    @DisplayName("Caso 2: Listar y buscar un registro existente (200 y datos persistidos)")
    void testListarYBuscarRegistroExistente() {
        PacienteRequestDTO req = new PacienteRequestDTO(
                "Carlos",
                "Tevez",
                "32999000",
                "+54 351 9990001",
                "carlos.tevez@ucc.edu.ar",
                "Swiss Medical"
        );
        PacienteResponseDTO creado = pacienteService.crear(req);

        PacienteResponseDTO encontrado = pacienteService.obtenerPorId(creado.id());
        assertEquals(creado.id(), encontrado.id());
        assertEquals("Carlos", encontrado.nombre());
        assertEquals("32999000", encontrado.dni());

        List<PacienteResponseDTO> lista = pacienteService.obtenerTodos();
        assertTrue(lista.stream().anyMatch(p -> p.id().equals(creado.id())));
    }

    @Test
    @DisplayName("Caso 3: Modificar y eliminar un registro (200 al modificar y 204 al eliminar)")
    void testModificarYEliminarRegistro() {
        PacienteRequestDTO req = new PacienteRequestDTO(
                "Martin",
                "Palermo",
                "29888777",
                "+54 351 8887776",
                "martin.palermo@ucc.edu.ar",
                "Medifé"
        );
        PacienteResponseDTO creado = pacienteService.crear(req);

        PacienteRequestDTO modReq = new PacienteRequestDTO(
                "Martin El Titan",
                "Palermo",
                "29888777",
                "+54 351 8887776",
                "titan.palermo@ucc.edu.ar",
                "OSDE"
        );
        PacienteResponseDTO modificado = pacienteService.modificar(creado.id(), modReq);
        assertEquals("Martin El Titan", modificado.nombre());
        assertEquals("titan.palermo@ucc.edu.ar", modificado.email());

        // Eliminar
        pacienteService.borrar(creado.id());

        // Verificar que ya no existe (404)
        assertThrows(PacienteNotFoundException.class, () -> pacienteService.obtenerPorId(creado.id()));
    }

    @Test
    @DisplayName("Caso 4: Validaciones de duplicados y reglas de negocio")
    void testValidacionDuplicados() {
        PacienteRequestDTO p1 = new PacienteRequestDTO(
                "Lionel", "Messi", "33111222", "111", "leo@ucc.edu.ar", "OSDE"
        );
        pacienteService.crear(p1);

        PacienteRequestDTO p2MismoDni = new PacienteRequestDTO(
                "Otro", "Lionel", "33111222", "222", "otro@ucc.edu.ar", "Apross"
        );
        ReglaDeNegocioException ex = assertThrows(ReglaDeNegocioException.class, () -> pacienteService.crear(p2MismoDni));
        assertTrue(ex.getMessage().contains("Ya existe un paciente"));
    }

    @Test
    @DisplayName("Caso 5: Buscar un ID inexistente o asociar una entidad inexistente (404)")
    void testBuscarOAsociarInexistente() {
        assertThrows(PacienteNotFoundException.class, () -> pacienteService.obtenerPorId(99999L));
        assertThrows(ProfesionalNotFoundException.class, () -> profesionalService.obtenerPorId(99999L));

        TurnoRequestDTO turnoConInexistente = new TurnoRequestDTO(
                LocalDateTime.now().plusDays(2),
                "Consulta inexistente",
                EstadoTurno.PENDIENTE,
                99999L,
                1L
        );
        assertThrows(PacienteNotFoundException.class, () -> turnoService.crear(turnoConInexistente));
    }

    @Test
    @DisplayName("Caso 6: Consultar registros mediante una relación (200 con resultados o lista vacía)")
    void testConsultarRegistrosPorRelacion() {
        PacienteResponseDTO paciente = pacienteService.crear(new PacienteRequestDTO(
                "Julian", "Alvarez", "42000111", "351111", "julian@ucc.edu.ar", "Apross"
        ));
        ProfesionalResponseDTO profesional = profesionalService.crear(new ProfesionalRequestDTO(
                "Diego", "Simeone", "MP-5555", "Traumatología"
        ));

        // Consulta de relación antes de tener turnos (lista vacía)
        List<TurnoResponseDTO> turnosVacios = turnoService.obtenerPorPaciente(paciente.id());
        assertTrue(turnosVacios.isEmpty());

        // Crear turno asociando ambos
        TurnoRequestDTO turnoReq = new TurnoRequestDTO(
                LocalDateTime.now().plusDays(3),
                "Revisión de tobillo",
                EstadoTurno.PENDIENTE,
                paciente.id(),
                profesional.id()
        );
        TurnoResponseDTO turnoCreado = turnoService.crear(turnoReq);
        assertNotNull(turnoCreado.id());

        // Consulta de relación con resultados
        List<TurnoResponseDTO> turnosDelPaciente = turnoService.obtenerPorPaciente(paciente.id());
        assertEquals(1, turnosDelPaciente.size());
        assertEquals("Revisión de tobillo", turnosDelPaciente.get(0).motivo());
        assertEquals(profesional.id(), turnosDelPaciente.get(0).profesional().id());

        List<TurnoResponseDTO> turnosDelProfesional = turnoService.obtenerPorProfesional(profesional.id());
        assertEquals(1, turnosDelProfesional.size());
        assertEquals(paciente.id(), turnosDelProfesional.get(0).paciente().id());
    }

    @Test
    @DisplayName("Caso 7: Integridad referencial - Impedir eliminación de entidad con turnos asociados")
    void testImpedirBorradoConTurnosAsociados() {
        PacienteResponseDTO paciente = pacienteService.crear(new PacienteRequestDTO(
                "Emiliano", "Martinez", "36444555", "351222", "dibu@ucc.edu.ar", "OSDE"
        ));
        ProfesionalResponseDTO profesional = profesionalService.crear(new ProfesionalRequestDTO(
                "Pablo", "Aimar", "MP-2121", "Cardiología"
        ));

        TurnoResponseDTO turno = turnoService.crear(new TurnoRequestDTO(
                LocalDateTime.now().plusDays(4),
                "Evaluación física",
                EstadoTurno.CONFIRMADO,
                paciente.id(),
                profesional.id()
        ));

        // Intento de borrar paciente con turnos
        ReglaDeNegocioException exPac = assertThrows(ReglaDeNegocioException.class, () -> pacienteService.borrar(paciente.id()));
        assertTrue(exPac.getMessage().contains("No se puede eliminar porque tiene turnos asociados"));

        // Intento de borrar profesional con turnos
        ReglaDeNegocioException exProf = assertThrows(ReglaDeNegocioException.class, () -> profesionalService.borrar(profesional.id()));
        assertTrue(exProf.getMessage().contains("No se puede eliminar porque tiene turnos asociados"));

        // Si eliminamos el turno primero, luego sí se puede eliminar el paciente
        turnoService.borrar(turno.id());
        assertDoesNotThrow(() -> pacienteService.borrar(paciente.id()));
    }
}
