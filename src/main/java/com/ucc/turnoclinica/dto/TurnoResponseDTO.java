package com.ucc.turnoclinica.dto;

import com.ucc.turnoclinica.entity.EstadoTurno;

import java.time.LocalDateTime;

public record TurnoResponseDTO(
        Long id,
        LocalDateTime fechaHora,
        String motivo,
        EstadoTurno estado,
        PacienteResponseDTO paciente,
        ProfesionalResponseDTO profesional
) {
}
