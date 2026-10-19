package com.ucc.turnoclinica.dto;

import java.time.LocalDateTime;

public record ErrorDTO(
        int status,
        String mensaje,
        LocalDateTime timestamp
) {
}
