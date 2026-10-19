package com.ucc.turnoclinica.exception;

public class ProfesionalNotFoundException extends RuntimeException {
    public ProfesionalNotFoundException(Long id) {
        super("No se encontró el profesional con ID: " + id);
    }

    public ProfesionalNotFoundException(String mensaje) {
        super(mensaje);
    }
}
