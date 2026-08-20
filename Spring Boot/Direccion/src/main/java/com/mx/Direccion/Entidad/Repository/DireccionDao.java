package com.mx.Direccion.Entidad.Repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.mx.Direccion.Entidad.Direccion;

public interface  DireccionDao extends JpaRepository<Direccion, Integer>{
	
}
