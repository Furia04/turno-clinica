package com.ucc.turnoclinica.controller;

import com.ucc.turnoclinica.dto.TurnoRequestDTO;
import com.ucc.turnoclinica.dto.TurnoResponseDTO;
import com.ucc.turnoclinica.service.TurnoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    @GetMapping
    public ResponseEntity<List<TurnoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(turnoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(turnoService.obtenerPorId(id));
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerPorPaciente(@PathVariable("idPaciente") Long idPaciente) {
        return ResponseEntity.ok(turnoService.obtenerPorPaciente(idPaciente));
    }

    @GetMapping("/profesional/{idProfesional}")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerPorProfesional(@PathVariable("idProfesional") Long idProfesional) {
        return ResponseEntity.ok(turnoService.obtenerPorProfesional(idProfesional));
    }

    @PostMapping
    public ResponseEntity<TurnoResponseDTO> crear(@Valid @RequestBody TurnoRequestDTO dto) {
        TurnoResponseDTO creado = turnoService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurnoResponseDTO> modificar(@PathVariable("id") Long id,
                                                      @Valid @RequestBody TurnoRequestDTO dto) {
        TurnoResponseDTO modificado = turnoService.modificar(id, dto);
        return ResponseEntity.ok(modificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        turnoService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
