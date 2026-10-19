package com.ucc.turnoclinica.dto;

public record PacienteResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String dni,
        String telefono,
        String email,
        String obraSocial
) {
}
