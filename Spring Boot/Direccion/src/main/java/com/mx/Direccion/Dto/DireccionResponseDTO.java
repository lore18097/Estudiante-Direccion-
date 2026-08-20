package com.mx.Direccion.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionResponseDTO {
	private Integer id;
    private String calle;
    private String colonia;
    private String ciudad;
}
