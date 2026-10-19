package com.ucc.turnoclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfesionalRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
        String apellido,

        @NotBlank(message = "La matrícula es obligatoria")
        @Size(min = 3, max = 30, message = "La matrícula debe tener entre 3 y 30 caracteres")
        String matricula,

        @NotBlank(message = "La especialidad es obligatoria")
        @Size(min = 2, max = 100, message = "La especialidad debe tener entre 2 y 100 caracteres")
        String especialidad
) {
}
