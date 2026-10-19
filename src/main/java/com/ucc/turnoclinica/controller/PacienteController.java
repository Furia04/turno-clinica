package com.ucc.turnoclinica.controller;

import com.ucc.turnoclinica.dto.PacienteRequestDTO;
import com.ucc.turnoclinica.dto.PacienteResponseDTO;
import com.ucc.turnoclinica.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(pacienteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(pacienteService.obtenerPorId(id));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<PacienteResponseDTO> buscarPorDni(@PathVariable("dni") String dni) {
        return ResponseEntity.ok(pacienteService.buscarPorDni(dni));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<PacienteResponseDTO>> buscarPorApellido(@RequestParam("apellido") String apellido) {
        return ResponseEntity.ok(pacienteService.buscarPorApellido(apellido));
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> crear(@Valid @RequestBody PacienteRequestDTO dto) {
        PacienteResponseDTO creado = pacienteService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> modificar(@PathVariable("id") Long id,
                                                         @Valid @RequestBody PacienteRequestDTO dto) {
        PacienteResponseDTO modificado = pacienteService.modificar(id, dto);
        return ResponseEntity.ok(modificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        pacienteService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
