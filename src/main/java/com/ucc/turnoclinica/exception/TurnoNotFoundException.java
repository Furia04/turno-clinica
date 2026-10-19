package com.ucc.turnoclinica.exception;

public class TurnoNotFoundException extends RuntimeException {
    public TurnoNotFoundException(Long id) {
        super("No se encontró el turno con ID: " + id);
    }

    public TurnoNotFoundException(String mensaje) {
        super(mensaje);
    }
}
