package com.mx.estudiante.Excepciones;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mx.estudiante.Dto.Respuesta;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    //ESTUDIANTE NO ENCONTRADO
    @ExceptionHandler(EstudianteNotFoundException.class)
    public ResponseEntity<Respuesta> handleEstudianteNotFoundException(EstudianteNotFoundException ex) {
        Respuesta respuesta = new Respuesta(ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }
    
    //ESTUDIANTE DUPLICADO
    @ExceptionHandler(EstudianteDuplicadoException.class)
    public ResponseEntity<Respuesta> handleEstudianteDuplicadoException(EstudianteDuplicadoException ex) {
        Respuesta respuesta = new Respuesta(ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
    }
    
    //DIRECCIÓN NO ENCONTRADA
    @ExceptionHandler(DireccionNoEncontradaException.class)
    public ResponseEntity<Respuesta> handleDireccionNoEncontradaException(DireccionNoEncontradaException ex) {
        Respuesta respuesta = new Respuesta(ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }
    
    //VALIDACIONES @VALID 
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Respuesta> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Respuesta respuesta = new Respuesta("Errores de validación", false, errors);
        return new ResponseEntity<>(respuesta, HttpStatus.BAD_REQUEST);
    }
    
    // ERRORES GENERICOS
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Respuesta> handleRuntimeException(RuntimeException ex) {
        Respuesta respuesta = new Respuesta("Error: " + ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Respuesta> handleGlobalException(Exception ex) {
        Respuesta respuesta = new Respuesta("Ha ocurrido un error inesperado", false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}