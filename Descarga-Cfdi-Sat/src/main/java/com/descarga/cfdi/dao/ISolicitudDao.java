package com.descarga.cfdi.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.descarga.cfdi.model.Solicitudes;


public interface ISolicitudDao extends CrudRepository<Solicitudes, Long> {

	@Query("select s from Solicitudes s where estatus = 2")
	public List<Solicitudes> getSolicitudesWithEstatusEnProceso();
}
