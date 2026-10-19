package com.ucc.turnoclinica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PacienteRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
        String apellido,

        @NotBlank(message = "El DNI es obligatorio")
        @Pattern(regexp = "\\d{7,10}", message = "El DNI debe ser numérico y contener entre 7 y 10 dígitos")
        String dni,

        @Pattern(regexp = "^[0-9+() -]*$", message = "El teléfono contiene caracteres no válidos")
        String telefono,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El formato de email no es válido")
        String email,

        @Size(max = 100, message = "La obra social no puede superar los 100 caracteres")
        String obraSocial
) {
}
