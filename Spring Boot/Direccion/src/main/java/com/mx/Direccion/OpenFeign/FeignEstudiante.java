package com.mx.Direccion.OpenFeign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mx.Direccion.Dto.EstudianteDTO;

@FeignClient(name = "ESTUDIANTE-SERVICE", url = "http://localhost:8000")
public interface FeignEstudiante {
    
    @GetMapping("/Estudiante/buscarPorDireccion")
    List<EstudianteDTO> buscarPorDireccion(@RequestParam("idDireccion") Integer idDireccion);
}