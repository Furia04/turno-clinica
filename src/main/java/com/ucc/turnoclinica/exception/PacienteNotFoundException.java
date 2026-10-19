package com.ucc.turnoclinica.exception;

public class PacienteNotFoundException extends RuntimeException {
    public PacienteNotFoundException(Long id) {
        super("No se encontró el paciente con ID: " + id);
    }

    public PacienteNotFoundException(String mensaje) {
        super(mensaje);
    }
}
