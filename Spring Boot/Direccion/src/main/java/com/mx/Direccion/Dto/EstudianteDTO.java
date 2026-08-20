package com.mx.Direccion.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteDTO {
    private Integer numeroControl;
    private String nombre;
    private String email;
    private String telefono;
    private Integer idDireccion;
}