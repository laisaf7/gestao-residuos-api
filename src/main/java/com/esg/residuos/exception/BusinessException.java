package com.esg.residuos.exception;

/** Lancada quando uma regra de negocio e violada (ex.: ponto inativo, status invalido). */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
