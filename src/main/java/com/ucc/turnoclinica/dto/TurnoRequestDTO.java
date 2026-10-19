package com.ucc.turnoclinica.dto;

import com.ucc.turnoclinica.entity.EstadoTurno;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TurnoRequestDTO(
        @NotNull(message = "La fecha y hora del turno es obligatoria")
        @Future(message = "La fecha y hora del turno no pueden ser pasadas")
        LocalDateTime fechaHora,

        @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
        String motivo,

        EstadoTurno estado,

        @NotNull(message = "El ID del paciente es obligatorio")
        Long idPaciente,

        @NotNull(message = "El ID del profesional es obligatorio")
        Long idProfesional
) {
}
