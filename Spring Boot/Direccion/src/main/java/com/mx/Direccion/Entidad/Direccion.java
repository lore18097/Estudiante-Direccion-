package com.mx.Direccion.Entidad;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name="DIRECCION")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Direccion {
	@Id
	private Integer id;
	private String calle;
	private String colonia;
	private String ciudad;
}
