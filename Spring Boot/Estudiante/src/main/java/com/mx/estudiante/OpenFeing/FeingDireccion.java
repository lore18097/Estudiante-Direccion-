package com.mx.estudiante.OpenFeing;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mx.estudiante.Dto.DireccionDTO;


@FeignClient(name="Direccion",url = "http://localhost:8002")
public interface FeingDireccion {
	@GetMapping("/Direccion/BuscarDirecto/{id}")
    DireccionDTO buscarDireccion(@PathVariable("id") Integer id);
}
