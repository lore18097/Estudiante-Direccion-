package com.mx.estudiante.Entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="ESTUDIANTE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {
	@Id
	private Integer numeroControl;
	private String nombre;
	private String email;
	private String telefono;
	@Column(name="IDDIRECCION")
	private Integer idDireccion;
}
