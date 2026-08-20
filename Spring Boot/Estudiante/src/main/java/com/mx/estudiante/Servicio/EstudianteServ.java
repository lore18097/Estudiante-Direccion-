package com.mx.estudiante.Servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mx.estudiante.Dto.DireccionDTO;
import com.mx.estudiante.Dto.EstudianteRequestDTO;
import com.mx.estudiante.Dto.EstudianteResponseDTO;
import com.mx.estudiante.Entidad.Estudiante;
import com.mx.estudiante.Entidad.Repository.EstudianteDao;
import com.mx.estudiante.Excepciones.DireccionNoEncontradaException;
import com.mx.estudiante.Excepciones.EstudianteDuplicadoException;
import com.mx.estudiante.Excepciones.EstudianteNotFoundException;
import com.mx.estudiante.OpenFeing.FeingDireccion;


@Service
public class EstudianteServ {
    
    @Autowired
    private EstudianteDao dao;
    
    @Autowired
    private FeingDireccion feignDireccion;
    
    public ResponseEntity<List<EstudianteResponseDTO>> listar() {
        // Aquí se usa dao.findAll() para obtener todos los estudiantes de la base de datos
        List<EstudianteResponseDTO> lista = dao.findAll().stream()
            .map(this::mapToDTO)
            .toList();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }
    
    @Transactional(readOnly = true)
    public ResponseEntity<EstudianteResponseDTO> buscar(Integer numeroControl) {
        // Aquí se usa dao.findById() para buscar un estudiante por su número de control
        Estudiante estudiante = dao.findById(numeroControl)
            .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado con número de control: " + numeroControl));
        return ResponseEntity.ok(mapToDTO(estudiante));
    }
    
    public ResponseEntity<List<EstudianteResponseDTO>> buscarPorDireccion(Integer idDireccion) {
        // Aquí se usa dao.findByIdDireccion() para buscar estudiantes por ID de dirección
        List<EstudianteResponseDTO> lista = dao.findByIdDireccion(idDireccion).stream()
            .map(this::mapToDTO)
            .toList();
        return ResponseEntity.ok(lista);
    }
    
    @Transactional
    public ResponseEntity<EstudianteResponseDTO> guardar(EstudianteRequestDTO dto) {
        // Aquí se usa dao.existsByNumeroControl() para validar que el número de control no exista
        if (dao.existsByNumeroControl(dto.getNumeroControl())) {
            throw new EstudianteDuplicadoException("Ya existe un estudiante con el número de control: " + dto.getNumeroControl());
        }
        // Aquí se usa dao.existsByEmail() para validar que el email no exista
        if (dao.existsByEmail(dto.getEmail())) {
            throw new EstudianteDuplicadoException("Ya existe un estudiante con el email: " + dto.getEmail());
        }
        // Aquí NO se usa DAO porque la dirección está en otro microservicio, se usa Feign
        try {
            DireccionDTO direccion = feignDireccion.buscarDireccion(dto.getIdDireccion());
            if (direccion == null || direccion.getId() == null) {
                throw new DireccionNoEncontradaException("La dirección con ID " + dto.getIdDireccion() + " no existe");
            }
        } catch (Exception e) {
            throw new DireccionNoEncontradaException("Error al validar la dirección: " + e.getMessage());
        }
        Estudiante estudiante = new Estudiante();
        estudiante.setNumeroControl(dto.getNumeroControl());
        estudiante.setNombre(dto.getNombre());
        estudiante.setEmail(dto.getEmail());
        estudiante.setTelefono(dto.getTelefono());
        estudiante.setIdDireccion(dto.getIdDireccion());
        // Aquí se usa dao.save() para guardar el nuevo estudiante en la base de datos
        Estudiante guardado = dao.save(estudiante);
        return ResponseEntity.ok(mapToDTO(guardado));
    }
    
    @Transactional
    public ResponseEntity<EstudianteResponseDTO> editar(Integer numeroControl, EstudianteRequestDTO dto) {
        // Aquí se usa dao.findById() para verificar que el estudiante existe
        Estudiante existente = dao.findById(numeroControl)
            .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado con número de control: " + numeroControl));
        // Aquí se usa dao.existsByEmailAndNumeroControlNot() para validar que el email no esté usado por otro estudiante
        if (dao.existsByEmailAndNumeroControlNot(dto.getEmail(), numeroControl)) {
            throw new EstudianteDuplicadoException("El email '" + dto.getEmail() + "' ya está registrado por otro estudiante");
        }
        // Aquí NO se usa DAO porque la dirección está en otro microservicio, se usa Feign
        try {
            DireccionDTO direccion = feignDireccion.buscarDireccion(dto.getIdDireccion());
            if (direccion == null || direccion.getId() == null) {
                throw new DireccionNoEncontradaException("La dirección con ID " + dto.getIdDireccion() + " no existe");
            }
        } catch (Exception e) {
            throw new DireccionNoEncontradaException("Error al validar la dirección: " + e.getMessage());
        }
        existente.setNombre(dto.getNombre());
        existente.setEmail(dto.getEmail());
        existente.setTelefono(dto.getTelefono());
        existente.setIdDireccion(dto.getIdDireccion());
        // Aquí se usa dao.save() para actualizar el estudiante en la base de datos
        Estudiante actualizado = dao.save(existente);
        return ResponseEntity.ok(mapToDTO(actualizado));
    }
    
    @Transactional
    public ResponseEntity<Void> eliminar(Integer numeroControl) {
        // Aquí se usa dao.findById() para verificar que el estudiante existe
        Estudiante estudiante = dao.findById(numeroControl)
            .orElseThrow(() -> new EstudianteNotFoundException("Estudiante no encontrado con número de control: " + numeroControl));
        // Aquí se usa dao.delete() para eliminar el estudiante de la base de datos
        dao.delete(estudiante);
        return ResponseEntity.ok().build();
    }
    
    private EstudianteResponseDTO mapToDTO(Estudiante est) {
        EstudianteResponseDTO dto = new EstudianteResponseDTO();
        dto.setNumeroControl(est.getNumeroControl());
        dto.setNombre(est.getNombre());
        dto.setEmail(est.getEmail());
        dto.setTelefono(est.getTelefono());
        // Aquí NO se usa DAO porque la dirección está en otro microservicio, se usa Feign
        try {
            if (est.getIdDireccion() != null) {
                DireccionDTO direccion = feignDireccion.buscarDireccion(est.getIdDireccion());
                dto.setDireccion(direccion);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener dirección para ID " + est.getIdDireccion() + ": " + e.getMessage());
            dto.setDireccion(null);
        }
        return dto;
    }
}