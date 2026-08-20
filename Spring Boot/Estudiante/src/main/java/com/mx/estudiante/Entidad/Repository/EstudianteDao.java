package com.mx.estudiante.Entidad.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mx.estudiante.Entidad.Estudiante;

@Repository
public interface EstudianteDao extends JpaRepository<Estudiante, Integer> {
    
    // Este método se usa en el servicio para buscar estudiantes por dirección
    // Se llama desde buscarPorDireccion()
    List<Estudiante> findByIdDireccion(Integer idDireccion);
    
    // Este método se usa en el servicio para buscar un estudiante por email
    // Se usa en guardar() para validar que el email no exista
    Optional<Estudiante> findByEmail(String email);
    
    // Este método se usa en el servicio para buscar un estudiante por nombre
    Optional<Estudiante> findByNombre(String nombre);
    
    // Este método se usa en guardar() para validar que el email no esté duplicado
    boolean existsByEmail(String email);
    
    // Este método se usa en guardar() para validar que el número de control no esté duplicado
    boolean existsByNumeroControl(Integer numeroControl);
    
    // Este método se usa en editar() para validar que el email no esté siendo usado por otro estudiante
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Estudiante e " +
           "WHERE e.email = :email AND e.numeroControl != :numeroControl")
    boolean existsByEmailAndNumeroControlNot(
            @Param("email") String email,
            @Param("numeroControl") Integer numeroControl);
}