package com.mx.Direccion.Excepciones;

public class DireccionEnUsoException extends RuntimeException {
	private static final long serialVersionUID=1L;
    public DireccionEnUsoException(String message) {
        super(message);
    }
}
