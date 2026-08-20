package com.mx.Direccion.Servicio;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mx.Direccion.Dto.DireccionRequestDTO;
import com.mx.Direccion.Dto.DireccionResponseDTO;
import com.mx.Direccion.Dto.EstudianteDTO;
import com.mx.Direccion.Entidad.Direccion;
import com.mx.Direccion.Entidad.Repository.DireccionDao;
import com.mx.Direccion.Excepciones.DireccionDuplicadaException;
import com.mx.Direccion.Excepciones.DireccionEnUsoException;
import com.mx.Direccion.Excepciones.ResourceNotFoundException;
import com.mx.Direccion.OpenFeign.FeignEstudiante;

import jakarta.validation.Valid;

@Service
public class DireccionServ {
    
    private final DireccionDao dao;
    private final FeignEstudiante feignEstudiante;
    
    // Este es el constructor que recibe el DAO y el cliente Feign como dependencias
    // Spring inyecta automáticamente estas dependencias cuando crea el bean del servicio
    public DireccionServ(DireccionDao dao, FeignEstudiante feignEstudiante) {
        this.dao = dao;
        this.feignEstudiante = feignEstudiante;
    }
    
    // Este método obtiene todas las direcciones de la base de datos
    // Usa dao.findAll() para obtener la lista completa de direcciones
    // Convierte cada entidad Direccion a DireccionResponseDTO usando el mapper convertirADto
    // Si la lista está vacía devuelve ResponseEntity.noContent() que es un código 204
    // Si hay direcciones devuelve ResponseEntity.ok con la lista de DTOs
    public ResponseEntity<List<DireccionResponseDTO>> listar() {
        List<DireccionResponseDTO> lista = dao.findAll().stream()
            .map(this::convertirADto)
            .toList();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }
    
    // Este método busca una dirección por su ID
    // Usa dao.findById() que devuelve un Optional de Direccion
    // Si la dirección no existe lanza ResourceNotFoundException con un mensaje descriptivo
    // Si existe convierte la entidad a DTO y la devuelve en ResponseEntity.ok
    // La anotación Transactional con readOnly true optimiza la consulta para solo lectura
    @Transactional(readOnly = true)
    public ResponseEntity<DireccionResponseDTO> buscar(Integer id) {
        Direccion direccion = dao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + id));
        return ResponseEntity.ok(convertirADto(direccion));
    }
    
    // Este método guarda una nueva dirección en la base de datos
    // Primero valida que no exista una dirección con la misma calle, colonia y ciudad
    // Recorre todas las direcciones existentes con dao.findAll() para verificar duplicados
    // Si encuentra una dirección duplicada lanza DireccionDuplicadaException
    // Si no hay duplicados crea una nueva entidad Direccion con los datos del DTO
    // El ID se genera automáticamente porque se pasa null en el constructor
    // Guarda la dirección con dao.save() y convierte la entidad guardada a DTO
    // Devuelve ResponseEntity.ok con el DTO de la dirección creada
    // La anotación Transactional asegura que la operación sea atómica
    @Transactional
    public ResponseEntity<DireccionResponseDTO> guardar(@Valid DireccionRequestDTO dto) {
        for (Direccion d : dao.findAll()) {
            if (d.getCalle().equalsIgnoreCase(dto.getCalle()) &&
                d.getColonia().equalsIgnoreCase(dto.getColonia()) &&
                d.getCiudad().equalsIgnoreCase(dto.getCiudad())) {
                throw new DireccionDuplicadaException(
                    "Ya existe una dirección con calle: " + dto.getCalle() + 
                    ", colonia: " + dto.getColonia() + 
                    " y ciudad: " + dto.getCiudad()
                );
            }
        }
        Direccion direccion = new Direccion(null, dto.getCalle(), dto.getColonia(), dto.getCiudad());
        Direccion guardada = dao.save(direccion);
        return ResponseEntity.ok(convertirADto(guardada));
    }
    
    // Este método actualiza una dirección existente en la base de datos
    // Primero verifica que la dirección existe usando dao.findById()
    // Si no existe lanza ResourceNotFoundException
    // Luego valida que no exista otra dirección con la misma calle, colonia y ciudad
    // Recorre todas las direcciones excluyendo el ID actual para evitar auto-duplicado
    // Si encuentra otra dirección duplicada lanza DireccionDuplicadaException
    // Si pasa las validaciones actualiza los campos de la dirección existente
    // Guarda los cambios con dao.save() que hace un UPDATE porque el ID ya existe
    // Devuelve ResponseEntity.ok con el DTO de la dirección actualizada
    // La anotación Transactional asegura que la operación sea atómica
    @Transactional
    public ResponseEntity<DireccionResponseDTO> editar(Integer id, @Valid DireccionRequestDTO dto) {
        Direccion existente = dao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + id));
        for (Direccion d : dao.findAll()) {
            if (d.getCalle().equalsIgnoreCase(dto.getCalle()) &&
                d.getColonia().equalsIgnoreCase(dto.getColonia()) &&
                d.getCiudad().equalsIgnoreCase(dto.getCiudad()) &&
                !d.getId().equals(id)) {
                throw new DireccionDuplicadaException(
                    "Ya existe otra dirección con calle: " + dto.getCalle() + 
                    ", colonia: " + dto.getColonia() + 
                    " y ciudad: " + dto.getCiudad()
                );
            }
        }
        existente.setCalle(dto.getCalle());
        existente.setColonia(dto.getColonia());
        existente.setCiudad(dto.getCiudad());
        Direccion actualizada = dao.save(existente);
        return ResponseEntity.ok(convertirADto(actualizada));
    }
    
    // Este método elimina una dirección de la base de datos
    // Primero verifica que la dirección existe usando dao.findById()
    // Si no existe lanza ResourceNotFoundException
    // Luego consulta el servicio de estudiantes usando Feign para verificar si la dirección está en uso
    // Llama a feignEstudiante.buscarPorDireccion(id) que retorna una lista de estudiantes
    // Si ocurre una excepción al consultar el servicio de estudiantes, captura el error y permite la eliminación
    // Si la lista de estudiantes no está vacía, lanza DireccionEnUsoException con la cantidad de estudiantes
    // Si no hay estudiantes usando la dirección, la elimina con dao.delete()
    // Devuelve ResponseEntity.ok() sin contenido
    // La anotación Transactional asegura que la operación sea atómica
    @Transactional
    public ResponseEntity<Void> eliminar(Integer id) {
        Direccion direccion = dao.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Dirección no encontrada con ID: " + id));
        List<EstudianteDTO> estudiantes;
        try {
            estudiantes = feignEstudiante.buscarPorDireccion(id);
        } catch (Exception e) {
            estudiantes = null;
        }
        if (estudiantes != null && !estudiantes.isEmpty()) {
            throw new DireccionEnUsoException(
                "No se puede eliminar la dirección porque está siendo usada por " + 
                estudiantes.size() + " estudiante(s)"
            );
        }
        dao.delete(direccion);
        return ResponseEntity.ok().build();
    }
    
    // Este método convierte una entidad Direccion a un objeto DireccionResponseDTO
    // Toma una entidad Direccion como parámetro y crea un nuevo DTO
    // Copia los valores id, calle, colonia y ciudad de la entidad al DTO
    // Devuelve el DTO listo para ser enviado como respuesta JSON
    // Este mapper se usa en todos los métodos que necesitan devolver una dirección
    private DireccionResponseDTO convertirADto(Direccion direccion) {
        DireccionResponseDTO dto = new DireccionResponseDTO();
        dto.setId(direccion.getId());
        dto.setCalle(direccion.getCalle());
        dto.setColonia(direccion.getColonia());
        dto.setCiudad(direccion.getCiudad());
        return dto;
    }
}