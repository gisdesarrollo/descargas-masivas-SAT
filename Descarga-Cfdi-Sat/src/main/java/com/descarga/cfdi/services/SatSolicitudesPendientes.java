package com.descarga.cfdi.services;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.descarga.cfdi.model.Clientes;
import com.descarga.cfdi.model.Solicitudes;
import com.descarga.cfdi.service.IClienteService;
import com.descarga.cfdi.service.ISolicitudService;
import com.descarga.cfdi.util.IDescargaMasivaSat;

//@Configuration
//@EnableScheduling
@Service
public class SatSolicitudesPendientes {

	protected final Logger LOG = Logger.getLogger(SatSolicitudesPendientes.class);
	
	@Value("${url.ws.sat.autenticacion}")
	private String urlWsAutenticacion;

	@Value("${url.soap.action.autenticacion}")
	private String soapActionAutenticacion;

	@Value("${url.ws.sat.solicitud}")
	private String urlWsSolicitud;

	@Value("${url.soap.action.solicitud}")
	private String soapActionSolicitud;

	@Value("${url.ws.sat.verificacion}")
	private String urlWsVerificacion;

	@Value("${url.soap.action.verificacion}")
	private String soapActionVerificacion;

	@Value("${url.ws.sat.descarga}")
	private String urlWsDescarga;

	@Value("${url.soap.action.descarga}")
	private String soapActionDescarga;

	@Autowired
	private IClienteService clienteService;

	@Autowired
	private IDescargaMasivaSat descargaMasivaSat;

	@Autowired
	private GetCfdiSatService cfdiSatService;

	@Autowired
	private ISolicitudService solicitudService;

	//@Scheduled(cron = "05 25 13 * * *", zone = "America/Mexico_City")
	public void dowloadCfdiByIdSolicitud() {
		byte[] cerByte = null;
		byte[] keyByte = null;
		String password = null;
		String rfcEmisor = null;
		int maxMinutos = 5;
		String token = null;
		String zipString = null;
		List<String> idPaquete = new ArrayList<String>();
		File cerFile = new File("D:\\Descargas(C)\\FIEL_GGI0807079QA_20220427152055\\FIEL_GGI0807079QA_20220427152055\\ggi0807079qa.cer");
		File keyFile = new
		 File("D:\\Descargas(C)\\FIEL_GGI0807079QA_20220427152055\\FIEL_GGI0807079QA_20220427152055\\Claveprivada_FIEL_GGI0807079QA_20220427_152055.key");
		password = "hermosillo7G";
		rfcEmisor = "GGI0807079QA";
		try {
			LOG.info("BUSCANDO SOLICITUDES SAT PENDIENTES PARA DESCARGA");
			List<Solicitudes> solicitudes = solicitudService.getSolicitudesWithEstatusEnProceso();
			if (solicitudes.isEmpty()) {
				LOG.info("No se encontraron solicitudes SAT pendientes de descarga");
			} else {
				for (Solicitudes solicitud : solicitudes) {
					if (solicitud.getIdSolicitud() != null) {
						String fechaInicialA = getFechaAuthenticacion(0);
						String fechaFinalA = getFechaAuthenticacion(maxMinutos);
						// randomUUID
						UUID uuid = UUID.randomUUID();
						//Clientes cliente = clienteService.getClienteByRfc(solicitud.getRfcCliente());
						//cerByte = cliente.getCer();
						//keyByte = cliente.getKey();
						//password = cliente.getPasswordKey();
						//rfcEmisor = cliente.getRfc();
						cerByte = FileUtils.readFileToByteArray(cerFile);
						 keyByte = FileUtils.readFileToByteArray(keyFile);
						// Authenticacion
						String messageAuthenticacion = descargaMasivaSat.getAuthenticacion(uuid, fechaInicialA,
								fechaFinalA, cerByte, keyByte, password);
						token = cfdiSatService.connectionSat(urlWsAutenticacion, soapActionAutenticacion,
								messageAuthenticacion, null, "authenticacion");
						if (token != null) {
							// Verifcación
							String messageVerificacion = descargaMasivaSat.getverificacion(rfcEmisor,
									solicitud.getIdSolicitud(), cerByte, keyByte, password);
							idPaquete = cfdiSatService.connectSatWSVerificacion(urlWsVerificacion,
									soapActionVerificacion, messageVerificacion, token, "verificacion",
									solicitud.getIdSolicitud(), rfcEmisor);
							if (!idPaquete.isEmpty()) {
								// Descarga
								if (idPaquete.size() > 0) {
									for (String paquete : idPaquete) {
										String messageDescarga = descargaMasivaSat.getDescarga(rfcEmisor, paquete,
												cerByte, keyByte, password);
										zipString = cfdiSatService.connectionSat(urlWsDescarga, soapActionDescarga,
												messageDescarga, token, "descarga");
										if (zipString != null) {
											descargaMasivaSat.decodeZip(zipString, rfcEmisor);
										} else {
											LOG.error("No se encontraron CFDIs para descargar");
										}
									}
									solicitudService.deleteById(solicitud.getId());
								}
							} else {
								LOG.error("Error idPaquete null");
							}
						} else {
							LOG.error("No se pudo obtener un token, token null");
						}
					}
				}
			}
			LOG.info("SOLICITUDES PENDIENTES DE CFDIs SAT FINALIZADO CORRECTAMENTE");
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error en tiempo de solicitudes pendientes de descarga..");
		}

	}

	public String getFechaAuthenticacion(int maxMinutos) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (maxMinutos > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + maxMinutos);
			return sdf.format(cal.getTime());
		}

		return sdf.format(new Date());
	}

}
