package com.mx.estudiante.Dto;

import lombok.Data;

@Data
public class DireccionResponseDTO {
    private Integer id;
    private String calle;
    private String colonia;
    private String ciudad;
}