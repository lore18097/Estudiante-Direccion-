package com.mx.Direccion.Excepciones;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mx.Direccion.Dto.Respuesta;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    // Este método maneja las excepciones de tipo ResourceNotFoundException
    // Se ejecuta cuando un recurso no se encuentra en la base de datos
    // Recibe la excepción como parámetro y extrae el mensaje de error
    // Crea un objeto Respuesta con el mensaje de la excepción y success en false
    // Devuelve un ResponseEntity con código HTTP 404 Not Found
    // El cuerpo de la respuesta contiene el objeto Respuesta con el mensaje de error
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Respuesta> handleResourceNotFoundException(ResourceNotFoundException ex) {
        Respuesta respuesta = new Respuesta(ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.NOT_FOUND);
    }
    
    // Este método maneja las excepciones de tipo DireccionDuplicadaException
    // Se ejecuta cuando se intenta crear o editar una dirección que ya existe
    // Recibe la excepción como parámetro y extrae el mensaje de error
    // Crea un objeto Respuesta con el mensaje de la excepción y success en false
    // Devuelve un ResponseEntity con código HTTP 409 Conflict
    // El código 409 indica que hay un conflicto con el estado actual del recurso
    @ExceptionHandler(DireccionDuplicadaException.class)
    public ResponseEntity<Respuesta> handleDireccionDuplicadaException(DireccionDuplicadaException ex) {
        Respuesta respuesta = new Respuesta(ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
    }
    
    // Este método maneja las excepciones de tipo DireccionEnUsoException
    // Se ejecuta cuando se intenta eliminar una dirección que está siendo usada por estudiantes
    // Recibe la excepción como parámetro y extrae el mensaje de error
    // Crea un objeto Respuesta con el mensaje de la excepción y success en false
    // Devuelve un ResponseEntity con código HTTP 409 Conflict
    // El código 409 indica que no se puede eliminar porque el recurso está en uso
    @ExceptionHandler(DireccionEnUsoException.class)
    public ResponseEntity<Respuesta> handleDireccionEnUsoException(DireccionEnUsoException ex) {
        Respuesta respuesta = new Respuesta(ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.CONFLICT);
    }
    
    // Este método maneja las excepciones de validación de campos con la anotación Valid
    // Se ejecuta cuando los datos enviados no cumplen con las validaciones del DTO
    // Recorre todos los errores de validación y los almacena en un mapa
    // Cada error contiene el nombre del campo y el mensaje de error correspondiente
    // Crea un objeto Respuesta con un mensaje genérico y el mapa de errores
    // Devuelve un ResponseEntity con código HTTP 400 Bad Request
    // El cuerpo de la respuesta contiene los detalles de cada campo inválido
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
    
    // Este método maneja cualquier excepción de tipo RuntimeException no capturada específicamente
    // Es un manejador genérico para errores inesperados en tiempo de ejecución
    // Recibe la excepción como parámetro y extrae el mensaje de error
    // Crea un objeto Respuesta con el mensaje de la excepción y success en false
    // Devuelve un ResponseEntity con código HTTP 500 Internal Server Error
    // Este manejador actúa como respaldo para excepciones no contempladas
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Respuesta> handleRuntimeException(RuntimeException ex) {
        Respuesta respuesta = new Respuesta("Error: " + ex.getMessage(), false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Este método maneja cualquier excepción general no capturada por los manejadores anteriores
    // Es el manejador de último recurso para errores inesperados
    // No muestra detalles específicos del error por seguridad
    // Crea un objeto Respuesta con un mensaje genérico y success en false
    // Devuelve un ResponseEntity con código HTTP 500 Internal Server Error
    // Este manejador asegura que siempre se devuelva una respuesta estructurada
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Respuesta> handleGlobalException(Exception ex) {
        Respuesta respuesta = new Respuesta("Ha ocurrido un error inesperado", false, null);
        return new ResponseEntity<>(respuesta, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}