package com.ucc.turnoclinica.controller;

import com.ucc.turnoclinica.dto.ProfesionalRequestDTO;
import com.ucc.turnoclinica.dto.ProfesionalResponseDTO;
import com.ucc.turnoclinica.service.ProfesionalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {

    private final ProfesionalService profesionalService;

    public ProfesionalController(ProfesionalService profesionalService) {
        this.profesionalService = profesionalService;
    }

    @GetMapping
    public ResponseEntity<List<ProfesionalResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(profesionalService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfesionalResponseDTO> obtenerPorId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(profesionalService.obtenerPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProfesionalResponseDTO>> buscarPorEspecialidad(@RequestParam("especialidad") String especialidad) {
        return ResponseEntity.ok(profesionalService.buscarPorEspecialidad(especialidad));
    }

    @PostMapping
    public ResponseEntity<ProfesionalResponseDTO> crear(@Valid @RequestBody ProfesionalRequestDTO dto) {
        ProfesionalResponseDTO creado = profesionalService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfesionalResponseDTO> modificar(@PathVariable("id") Long id,
                                                            @Valid @RequestBody ProfesionalRequestDTO dto) {
        ProfesionalResponseDTO modificado = profesionalService.modificar(id, dto);
        return ResponseEntity.ok(modificado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") Long id) {
        profesionalService.borrar(id);
        return ResponseEntity.noContent().build();
    }
}
