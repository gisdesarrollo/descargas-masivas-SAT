package com.descarga.cfdi.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "cat_clientes")
public class Clientes implements Serializable {

	/**
    *
    */
   private static final long serialVersionUID = 1L;

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "Id")
   private Long id;

   @Column(name = "Nombre")
   private String nombre;
   
   @Column(name = "Status")
   private int status;
   
   @Column(name="MailConfirmacion")
   private String email;
   
   @Column(name="NombreLogo")
   private String nombreLogo;
   
   @Lob
   @Column(name="Logo")
   private byte[] logo;
   
   @Column(name="RazonSocial")
   private String razonSocial;
   
   @Column(name = "Rfc")
   private String rfc;
   
   @Column(name="CodigoPostal")
   private int codigoPostal;
   
   @Column(name ="Pais")
   private int pais;
   
   @Column(name="NombreArchivoCer")
   private String nombreFileCer;
   
   @Lob
   @Column(name="Cer")
   private byte[] cer;
   
   @Column(name="NombreArchivoKey")
   private String nombreFileKey;
   @Lob
   @Column(name="`Key`")
   private byte[] key;
   
   @Column(name="PasswordKey")
   private String passwordKey;
   
   @Column(name = "Servidor")
   private String servidor;

   @Column(name = "KeyXsa")
   private String keyXsa;

   @Column(name = "FechaInicial")
   private Date fechaInicial; 
   
   @Column(name = "FechaInicialSat")
   private Date fechaInicialSat;
   
   public Clientes() {
   	
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNombreLogo() {
		return nombreLogo;
	}

	public void setNombreLogo(String nombreLogo) {
		this.nombreLogo = nombreLogo;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public int getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal = codigoPostal;
	}

	public int getPais() {
		return pais;
	}

	public void setPais(int pais) {
		this.pais = pais;
	}

	public String getNombreFileCer() {
		return nombreFileCer;
	}

	public void setNombreFileCer(String nombreFileCer) {
		this.nombreFileCer = nombreFileCer;
	}

	public byte[] getCer() {
		return cer;
	}

	public void setCer(byte[] cer) {
		this.cer = cer;
	}

	public String getNombreFileKey() {
		return nombreFileKey;
	}

	public void setNombreFileKey(String nombreFileKey) {
		this.nombreFileKey = nombreFileKey;
	}

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public String getPasswordKey() {
		return passwordKey;
	}

	public void setPasswordKey(String passwordKey) {
		this.passwordKey = passwordKey;
	}

	public String getServidor() {
		return servidor;
	}

	public void setServidor(String servidor) {
		this.servidor = servidor;
	}

	public String getKeyXsa() {
		return keyXsa;
	}

	public void setKeyXsa(String keyXsa) {
		this.keyXsa = keyXsa;
	}

	public Date getFechaInicial() {
		return fechaInicial;
	}

	public void setFechaInicial(Date fechaInicial) {
		this.fechaInicial = fechaInicial;
	}

	public Date getFechaInicialSat() {
		return fechaInicialSat;
	}

	public void setFechaInicialSat(Date fechaInicialSat) {
		this.fechaInicialSat = fechaInicialSat;
	}
   
}
