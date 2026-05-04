package com.ticketti.ms_donaciones.exception;

/**
 * Se lanza cuando se viola una regla de negocio.
 * Ejemplo: intentar registrar una org con RUT duplicado
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String mensaje) {
        super(mensaje);
    }
}