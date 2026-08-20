package com.mx.Direccion.Excepciones;

public class DireccionDuplicadaException  extends RuntimeException {
	private static final long serialVersionUID=1L;
    public DireccionDuplicadaException(String message) {
        super(message);
    }
}
