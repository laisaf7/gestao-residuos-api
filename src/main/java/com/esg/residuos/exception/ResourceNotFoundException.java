package com.esg.residuos.exception;

/** Lancada quando um recurso solicitado nao e encontrado na base de dados. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
