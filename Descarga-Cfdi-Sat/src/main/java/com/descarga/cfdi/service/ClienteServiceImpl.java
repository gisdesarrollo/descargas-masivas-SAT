package com.descarga.cfdi.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.descarga.cfdi.dao.IClienteDao;
import com.descarga.cfdi.model.Clientes;


@Service
public class ClienteServiceImpl implements IClienteService {

	@Autowired
	private IClienteDao clienteDao;
	
	@Override
	public List<Clientes> getActiveClientes() {
		return clienteDao.getActiveClientes();
	}

	@Override
	@Transactional
	public void updateFechaInicialClienteById(Date fecha, Long id) {
		clienteDao.updateFechaInicialClienteById(fecha, id);
		
	}

	@Override
	public Clientes findById(Long id) {
		return clienteDao.findById(id).orElse(null);
	}

	@Override
	public Clientes getClienteByRfc(String rfc) {
		return clienteDao.getClienteByRfc(rfc);
	}

	@Override
	public void save(Clientes cliente) {
		clienteDao.save(cliente);
	}


	@Override
	public void delete(Long id) {
		clienteDao.deleteById(id);
		
	}

	@Override
	public List<Long> getIdClienteByUsername(String username) {
		return clienteDao.getIdClienteByUsername(username);
	}

	@Override
	public List<Clientes> getClientesByUsername(String username) {
		return clienteDao.getClientesByUsername(username);
	}

	@Override
	public List<Clientes> getClienteWithIn(List<Long> cliente) {
		return clienteDao.getClienteWithIn(cliente);
	}

}
