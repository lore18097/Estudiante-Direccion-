package com.mx.estudiante.Excepciones;

public class DireccionNoEncontradaException extends RuntimeException {
	private static final long serialVersionUID=1L;
	public DireccionNoEncontradaException(String message) {
        super(message);
    }
}
