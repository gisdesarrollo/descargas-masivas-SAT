package com.descarga.cfdi.service;

import java.util.Date;
import java.util.List;

import com.descarga.cfdi.model.Clientes;

public interface IClienteService {

public List<Clientes> getActiveClientes();
	
	public void updateFechaInicialClienteById(Date fecha, Long id);
	
	public Clientes findById(Long id);

	public Clientes getClienteByRfc(String rfc);
	
	public void save(Clientes cliente);
	
	public void delete(Long id);
	
	public List<Long> getIdClienteByUsername(String username);
	 
	public List<Clientes> getClientesByUsername(String username);
	
	 public List<Clientes> getClienteWithIn(List<Long> cliente);
}
