package com.ucc.turnoclinica.service;

import com.ucc.turnoclinica.dto.PacienteRequestDTO;
import com.ucc.turnoclinica.dto.PacienteResponseDTO;
import com.ucc.turnoclinica.entity.Paciente;
import com.ucc.turnoclinica.exception.PacienteNotFoundException;
import com.ucc.turnoclinica.exception.ReglaDeNegocioException;
import com.ucc.turnoclinica.mapper.PacienteMapper;
import com.ucc.turnoclinica.repository.PacienteRepository;
import com.ucc.turnoclinica.repository.TurnoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final TurnoRepository turnoRepository;
    private final PacienteMapper pacienteMapper;

    public PacienteService(PacienteRepository pacienteRepository,
                           TurnoRepository turnoRepository,
                           PacienteMapper pacienteMapper) {
        this.pacienteRepository = pacienteRepository;
        this.turnoRepository = turnoRepository;
        this.pacienteMapper = pacienteMapper;
    }

    public List<PacienteResponseDTO> obtenerTodos() {
        return pacienteRepository.findAll().stream()
                .map(pacienteMapper::entityToDTO)
                .toList();
    }

    public PacienteResponseDTO obtenerPorId(Long id) {
        Paciente paciente = buscarEntidadPorId(id);
        return pacienteMapper.entityToDTO(paciente);
    }

    public PacienteResponseDTO buscarPorDni(String dni) {
        Paciente paciente = pacienteRepository.findByDni(dni)
                .orElseThrow(() -> new PacienteNotFoundException("No se encontró el paciente con DNI: " + dni));
        return pacienteMapper.entityToDTO(paciente);
    }

    public List<PacienteResponseDTO> buscarPorApellido(String apellido) {
        return pacienteRepository.findByApellidoContainingIgnoreCase(apellido).stream()
                .map(pacienteMapper::entityToDTO)
                .toList();
    }

    public PacienteResponseDTO crear(PacienteRequestDTO dto) {
        if (pacienteRepository.existsByDni(dto.dni().trim())) {
            throw new ReglaDeNegocioException("Ya existe un paciente registrado con ese DNI.");
        }
        Paciente paciente = pacienteMapper.dtoToEntity(dto);
        Paciente guardado = pacienteRepository.save(paciente);
        return pacienteMapper.entityToDTO(guardado);
    }

    public PacienteResponseDTO modificar(Long id, PacienteRequestDTO dto) {
        Paciente paciente = buscarEntidadPorId(id);

        String nuevoDni = dto.dni().trim();
        if (!paciente.getDni().equalsIgnoreCase(nuevoDni) && pacienteRepository.existsByDni(nuevoDni)) {
            throw new ReglaDeNegocioException("Ya existe un paciente registrado con ese DNI.");
        }

        paciente.setNombre(dto.nombre().trim());
        paciente.setApellido(dto.apellido().trim());
        paciente.setDni(nuevoDni);
        paciente.setTelefono(dto.telefono() != null ? dto.telefono().trim() : null);
        paciente.setEmail(dto.email().trim().toLowerCase());
        paciente.setObraSocial(dto.obraSocial() != null ? dto.obraSocial().trim() : null);

        Paciente guardado = pacienteRepository.save(paciente);
        return pacienteMapper.entityToDTO(guardado);
    }

    public void borrar(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new PacienteNotFoundException(id);
        }
        if (turnoRepository.existsByPaciente_Id(id)) {
            throw new ReglaDeNegocioException("No se puede eliminar porque tiene turnos asociados.");
        }
        pacienteRepository.deleteById(id);
    }

    public Paciente buscarEntidadPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNotFoundException(id));
    }
}
