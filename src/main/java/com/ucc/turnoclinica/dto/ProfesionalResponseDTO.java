package com.ucc.turnoclinica.dto;

public record ProfesionalResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String matricula,
        String especialidad
) {
}
