package com.mx.estudiante.Excepciones;

public class EstudianteNotFoundException extends RuntimeException {
	private static final long serialVersionUID=1L;
	public EstudianteNotFoundException(String message) {
        super(message);
    }
}
