package com.descarga.cfdi.service;

import java.util.List;

import com.descarga.cfdi.model.Solicitudes;


public interface ISolicitudService {

public void save(Solicitudes solicitud);
	
	public List<Solicitudes> getSolicitudesWithEstatusEnProceso();
	
	public void deleteById(Long id);
}
