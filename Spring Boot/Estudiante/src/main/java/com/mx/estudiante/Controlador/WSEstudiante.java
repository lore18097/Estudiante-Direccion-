package com.mx.estudiante.Controlador;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.estudiante.Dto.EstudianteRequestDTO;
import com.mx.estudiante.Dto.EstudianteResponseDTO;
import com.mx.estudiante.Dto.Respuesta;
import com.mx.estudiante.Servicio.EstudianteServ;

import jakarta.validation.Valid;

@RequestMapping("Estudiante")
@RestController
@CrossOrigin(origins = "*")
public class WSEstudiante {
    
    @Autowired
    private EstudianteServ service;
    
    // Este método maneja la petición GET para listar todos los estudiantes
    // Llama al método listar del servicio y envuelve la respuesta en el DTO Respuesta
    // Si no hay estudiantes registrados devuelve un mensaje indicando que no hay datos
    @GetMapping("Listar")
    public ResponseEntity<Respuesta> listar() {
        ResponseEntity<List<EstudianteResponseDTO>> responseEntity = service.listar();
        if (responseEntity.getStatusCode() == HttpStatus.NO_CONTENT) {
            Respuesta respuesta = new Respuesta("No hay estudiantes registrados", true, null);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        }
        Respuesta respuesta = new Respuesta("Estudiantes obtenidos exitosamente", true, responseEntity.getBody());
        return ResponseEntity.ok(respuesta);
    }
    
    // Este método maneja la petición GET para buscar un estudiante por su número de control
    // El número de control se recibe como variable en la URL
    // Llama al método buscar del servicio y devuelve el estudiante encontrado
    @GetMapping("Buscar/{numeroControl}")
    public ResponseEntity<Respuesta> buscar(@PathVariable Integer numeroControl) {
        ResponseEntity<EstudianteResponseDTO> responseEntity = service.buscar(numeroControl);
        Respuesta respuesta = new Respuesta("Estudiante obtenido exitosamente", true, responseEntity.getBody());
        return ResponseEntity.ok(respuesta);
    }
    
    // Este método maneja la petición GET para buscar estudiantes por ID de dirección
    // El ID de dirección se recibe como parámetro en la URL
    // Imprime en consola la cantidad de estudiantes encontrados para depuración
    // Llama al método buscarPorDireccion del servicio y devuelve la lista de estudiantes
    @GetMapping("buscarPorDireccion")
    public ResponseEntity<Respuesta> buscarPorDireccion(@RequestParam Integer idDireccion) {
        System.out.println("=== BUSCANDO ESTUDIANTES POR DIRECCIÓN ID: " + idDireccion + " ===");
        ResponseEntity<List<EstudianteResponseDTO>> responseEntity = service.buscarPorDireccion(idDireccion);
        System.out.println("Estudiantes encontrados: " + (responseEntity.getBody() != null ? responseEntity.getBody().size() : 0));
        Respuesta respuesta = new Respuesta("Estudiantes encontrados por dirección", true, responseEntity.getBody());
        return ResponseEntity.ok(respuesta);
    }
    
    // Este método maneja la petición POST para guardar un nuevo estudiante
    // Los datos del estudiante se reciben en el cuerpo de la petición como JSON
    // La anotación @Valid valida automáticamente los campos del DTO
    // Llama al método guardar del servicio y devuelve el estudiante creado
    @PostMapping("Guardar")
    public ResponseEntity<Respuesta> guardar(@Valid @RequestBody EstudianteRequestDTO dto) {
        ResponseEntity<EstudianteResponseDTO> responseEntity = service.guardar(dto);
        Respuesta respuesta = new Respuesta("Estudiante creado exitosamente", true, responseEntity.getBody());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
    
    // Este método maneja la petición PUT para actualizar un estudiante existente
    // El número de control se recibe como variable en la URL
    // Los datos actualizados se reciben en el cuerpo de la petición como JSON
    // La anotación @Valid valida automáticamente los campos del DTO
    // Llama al método editar del servicio y devuelve el estudiante actualizado
    @PutMapping("Editar/{numeroControl}")
    public ResponseEntity<Respuesta> editar(
            @PathVariable Integer numeroControl,
            @Valid @RequestBody EstudianteRequestDTO dto) {
        ResponseEntity<EstudianteResponseDTO> responseEntity = service.editar(numeroControl, dto);
        Respuesta respuesta = new Respuesta("Estudiante actualizado exitosamente", true, responseEntity.getBody());
        return ResponseEntity.ok(respuesta);
    }
    
    // Este método maneja la petición DELETE para eliminar un estudiante
    // El número de control se recibe como variable en la URL
    // Llama al método eliminar del servicio y devuelve un mensaje de éxito
    @DeleteMapping("Eliminar/{numeroControl}")
    public ResponseEntity<Respuesta> eliminar(@PathVariable Integer numeroControl) {
        service.eliminar(numeroControl);
        Respuesta respuesta = new Respuesta("Estudiante eliminado exitosamente", true, null);
        return ResponseEntity.ok(respuesta);
    }
}
