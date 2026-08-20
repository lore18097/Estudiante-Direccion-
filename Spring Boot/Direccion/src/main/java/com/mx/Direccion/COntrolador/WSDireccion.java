package com.mx.Direccion.COntrolador;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mx.Direccion.Dto.DireccionRequestDTO;
import com.mx.Direccion.Dto.DireccionResponseDTO;
import com.mx.Direccion.Dto.Respuesta;
import com.mx.Direccion.Servicio.DireccionServ;

import jakarta.validation.Valid;

@RestController
@RequestMapping("Direccion")
@CrossOrigin(origins = "*")
public class WSDireccion {
    
    @Autowired
    private DireccionServ service;
    
    // Este método maneja la petición GET para listar todas las direcciones
    // Llama al método listar del servicio que devuelve un ResponseEntity con la lista de direcciones
    // Si el servicio devuelve NO_CONTENT significa que no hay direcciones registradas
    // En ese caso crea una respuesta con un mensaje indicando que no hay direcciones
    // Si hay direcciones, envuelve la lista en el DTO Respuesta con un mensaje de éxito
    // Devuelve un ResponseEntity con código HTTP 200 OK
    @GetMapping("Listar")
    public ResponseEntity<Respuesta> listar() {
        ResponseEntity<List<DireccionResponseDTO>> responseEntity = service.listar();
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            Respuesta respuesta = new Respuesta("No hay direcciones registradas", true, null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        Respuesta respuesta = new Respuesta("Direcciones obtenidas exitosamente", true, responseEntity.getBody());
        return ResponseEntity.ok(respuesta);
    }
    
    // Este método maneja la petición GET para buscar una dirección por su ID
    // El ID se recibe como variable en la URL
    // Llama al método buscar del servicio que devuelve la dirección encontrada
    // Envuelve la dirección en el DTO Respuesta con un mensaje de éxito
    // Devuelve un ResponseEntity con código HTTP 200 OK
    @GetMapping("Buscar/{id}")
    public ResponseEntity<Respuesta> buscar(@PathVariable Integer id) {
        ResponseEntity<DireccionResponseDTO> responseEntity = service.buscar(id);
        Respuesta respuesta = new Respuesta("Dirección obtenida exitosamente", true, responseEntity.getBody());
        return ResponseEntity.ok(respuesta);
    }
    
    // Este método es un endpoint especial para que lo use el servicio de Estudiantes a través de Feign
    // No usa el DTO Respuesta, devuelve directamente el DTO de dirección
    // Esto permite que el microservicio de Estudiantes obtenga la dirección sin el wrapper
    // El ID se recibe como variable en la URL
    // Llama al método buscar del servicio que devuelve la dirección encontrada
    // Devuelve un ResponseEntity con el DTO de dirección directamente
    // Este endpoint no está envuelto en Respuesta para facilitar la comunicación entre microservicios
    @GetMapping("BuscarDirecto/{id}")
    public ResponseEntity<DireccionResponseDTO> buscarDirecto(@PathVariable Integer id) {
        ResponseEntity<DireccionResponseDTO> response = service.buscar(id);
        return response;
    }
    
    // Este método maneja la petición POST para guardar una nueva dirección
    // Los datos de la dirección se reciben en el cuerpo de la petición como JSON
    // La anotación Valid valida automáticamente los campos del DTO
    // Llama al método guardar del servicio que crea la dirección en la base de datos
    // Envuelve la dirección creada en el DTO Respuesta con un mensaje de éxito
    // Devuelve un ResponseEntity con código HTTP 201 Created
    @PostMapping("Guardar")
    public ResponseEntity<Respuesta> guardar(@Valid @RequestBody DireccionRequestDTO dto) {
        ResponseEntity<DireccionResponseDTO> responseEntity = service.guardar(dto);
        Respuesta respuesta = new Respuesta("Dirección creada exitosamente", true, responseEntity.getBody());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
    
    // Este método maneja la petición PUT para actualizar una dirección existente
    // El ID se recibe como variable en la URL
    // Los datos actualizados se reciben en el cuerpo de la petición como JSON
    // La anotación Valid valida automáticamente los campos del DTO
    // Llama al método editar del servicio que actualiza la dirección en la base de datos
    // Envuelve la dirección actualizada en el DTO Respuesta con un mensaje de éxito
    // Devuelve un ResponseEntity con código HTTP 200 OK
    @PutMapping("Editar/{id}")
    public ResponseEntity<Respuesta> editar(
            @PathVariable Integer id, 
            @Valid @RequestBody DireccionRequestDTO dto) {
        ResponseEntity<DireccionResponseDTO> responseEntity = service.editar(id, dto);
        Respuesta respuesta = new Respuesta("Dirección actualizada exitosamente", true, responseEntity.getBody());
        return ResponseEntity.ok(respuesta);
    }
    
    // Este método maneja la petición DELETE para eliminar una dirección
    // El ID se recibe como variable en la URL
    // Llama al método eliminar del servicio que borra la dirección de la base de datos
    // Si la dirección está siendo usada por estudiantes, el servicio lanza una excepción
    // Envuelve un mensaje de éxito en el DTO Respuesta con datos nulos
    // Devuelve un ResponseEntity con código HTTP 200 OK
    @DeleteMapping("Eliminar/{id}")
    public ResponseEntity<Respuesta> eliminar(@PathVariable Integer id) {
        service.eliminar(id);
        Respuesta respuesta = new Respuesta("Dirección eliminada exitosamente", true, null);
        return ResponseEntity.ok(respuesta);
    }
}