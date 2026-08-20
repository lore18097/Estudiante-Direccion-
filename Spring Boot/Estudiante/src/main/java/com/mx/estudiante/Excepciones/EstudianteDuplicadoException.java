package com.mx.estudiante.Excepciones;

public class EstudianteDuplicadoException extends RuntimeException {
	private static final long serialVersionUID=1L;
	public EstudianteDuplicadoException(String message) {
        super(message);
    }
}
