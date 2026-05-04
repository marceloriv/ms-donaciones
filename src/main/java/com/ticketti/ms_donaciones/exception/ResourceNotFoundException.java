package com.ticketti.ms_donaciones.exception;

/**
 * Se lanza cuando no se encuentra un recurso por su ID.
 * Ejemplo: GET /organizaciones/99 y no existe id=99
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ResourceNotFoundException(String recurso, Long id) {
        super(recurso + " con id " + id + " no encontrado");
    }
}