package com.descarga.cfdi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.descarga.cfdi.dao.ISolicitudDao;
import com.descarga.cfdi.model.Solicitudes;


@Service
public class SolicitudServiceImpl implements ISolicitudService {

	@Autowired
	private ISolicitudDao solicitudDao;
	
	@Override
	public void save(Solicitudes solicitud) {
		solicitudDao.save(solicitud);
	}

	@Override
	public List<Solicitudes> getSolicitudesWithEstatusEnProceso() {
		return solicitudDao.getSolicitudesWithEstatusEnProceso();
	}

	@Override
	public void deleteById(Long id) {
		solicitudDao.deleteById(id);
		
	}
}
