package com.descarga.cfdi.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.descarga.cfdi.model.Clientes;


public interface IClienteDao extends CrudRepository<Clientes, Long> {
	
	 @Query("select s from Clientes s where s.status = 1")
	    public List<Clientes> getActiveClientes();
	 
	 @Modifying
		@Query("update Clientes e set e.fechaInicial = :fecha where e.id = :id")
		public void updateFechaInicialClienteById(@Param ("fecha") Date fecha, @Param("id") Long id);
	 
	 @Query("select s from Clientes s where s.rfc = :rfc ")
	    public Clientes getClienteByRfc(@Param("rfc")String rfc);
	 
	 @Query(value = "select c.Id  from cat_usuarios u "
	 		+ "join rel_usuarios_clientes relUC on (u.id_usuario=relUC.id_usuario) "
	 		+ "join cat_clientes c on (relUC.id_cliente=c.Id) "
	 		+ "where u.username=?1",nativeQuery = true)
	 public List<Long> getIdClienteByUsername(String username);
	 
	 @Query(value = "select c.*  from cat_usuarios u "
		 		+ "join rel_usuarios_clientes relUC on (u.id_usuario=relUC.id_usuario) "
		 		+ "join cat_clientes c on (relUC.id_cliente=c.Id) "
		 		+ "where u.username=?1",nativeQuery = true)
		 public List<Clientes> getClientesByUsername(String username);
	
	 @Query("select c from Clientes c where c.id IN :cliente")
	 public List<Clientes> getClienteWithIn(@Param("cliente")List<Long> cliente);
	 
	 @Query("select c from Clientes c where c.id=?1")
	 public Clientes getClienteById(Long id);
}
