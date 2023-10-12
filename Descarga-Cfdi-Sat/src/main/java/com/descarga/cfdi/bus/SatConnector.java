package com.descarga.cfdi.bus;

import java.io.File;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.descarga.cfdi.model.Clientes;
import com.descarga.cfdi.service.IClienteService;
import com.descarga.cfdi.service.ISolicitudService;
import com.descarga.cfdi.services.GetCfdiSatService;
import com.descarga.cfdi.services.SatSolicitudesPendientes;
import com.descarga.cfdi.util.IDescargaMasivaSat;

@Configuration
@EnableScheduling
public class SatConnector {
	
protected final Logger LOG = Logger.getLogger(SatConnector.class);
	
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
	private SatSolicitudesPendientes solicitudPendiente;
	//private ISolicitudService solicitudService;

	@Scheduled(cron = "40 05 12 * * *", zone = "America/Mexico_City")
	public void dowloadCfdiSat() throws CertificateEncodingException, IOException {

		byte[] cerByte = null;
		byte[] keyByte = null;
		String password = null;
		String rfcEmisor = null;
		String tipoSolicitud = "CFDI";
		String token = null;
		String idSolicitud = null;
		List<String> idPaquete = new ArrayList<String>();
		String zipString = null;
		int maxMinutos = 5;
		try {
			LOG.info("INICIANDO DESCARGAS MASIVA DE CFDIs POR SAT...");
			List<Clientes> clientes = clienteService.getActiveClientes();
			File cerFile = new File("D:\\Descargas(C)\\FIEL_GGI0807079QA_20220427152055\\FIEL_GGI0807079QA_20220427152055\\ggi0807079qa.cer");
			File keyFile = new
			 File("D:\\Descargas(C)\\FIEL_GGI0807079QA_20220427152055\\FIEL_GGI0807079QA_20220427152055\\Claveprivada_FIEL_GGI0807079QA_20220427_152055.key");
			password = "hermosillo7G";
			rfcEmisor = "GGI0807079QA";
			for (Clientes cliente : clientes) {
				if(cliente.getId() == 1) {
				if (cliente.getServidor() != null) {
					/*produccion*/
					//cerByte = cliente.getCer();
					//keyByte = cliente.getKey();
					//password = cliente.getPasswordKey();
					//rfcEmisor = cliente.getRfc();
					/**/
					/*local*/
						 cerByte = FileUtils.readFileToByteArray(cerFile);
						 keyByte = FileUtils.readFileToByteArray(keyFile);
						// get fecha inicial y final authenticacion
					/**/
					String fechaInicialA = getFechaAuthenticacion(0);
					String fechaFinalA = getFechaAuthenticacion(maxMinutos);
					// randomUUID
					UUID uuid = UUID.randomUUID();
					// Authenticacion
					String messageAuthenticacion = descargaMasivaSat.getAuthenticacion(uuid, fechaInicialA, fechaFinalA,
							cerByte, keyByte, password);
					token = cfdiSatService.connectionSat(urlWsAutenticacion, soapActionAutenticacion,
							messageAuthenticacion, null, "authenticacion");
					if (token != null) {
						// Solicitud
						String fechaInicialS = getFechaSolicitud(cliente.getFechaInicialSat(), true);
						String fechaFinalS = getFechaSolicitud(new Date(), false);

						String messageSolicitud = descargaMasivaSat.getSolicitud(fechaInicialS, fechaFinalS, rfcEmisor,
								tipoSolicitud, cerByte, keyByte, password);
						idSolicitud = cfdiSatService.connectionSat(urlWsSolicitud, soapActionSolicitud,
								messageSolicitud, token, "solicitud");
						if (idSolicitud != null) {
							// Verifcación
							String messageVerificacion = descargaMasivaSat.getverificacion(rfcEmisor, idSolicitud,
									cerByte, keyByte, password);
							idPaquete = cfdiSatService.connectSatWSVerificacion(urlWsVerificacion,
									soapActionVerificacion, messageVerificacion, token, "verificacion", idSolicitud,
									rfcEmisor);
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
								}
							} else {
								LOG.error("Error idPaquete null");
							}
						} else {
							LOG.error("Error idSolicitud null");
						}

					} else {
						LOG.error("Error Error token null");
					}
				}
			}
			}
			
			LOG.info("DESCARGAS CFDIs SAT FINALIZADO CORRECTAMENTE...");
			//Thread.sleep(1000);
			TimeUnit.MINUTES.sleep(1);
			LOG.info("INICIANDO BUSQUEDA DE SOLICITUDES PENDIENTES POR DESCARGAR...");	
			solicitudPendiente.dowloadCfdiByIdSolicitud();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error al iniciar las descargas Masivas CFDIs SAT");
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

	public String getFechaSolicitud(Date fecha, boolean fechaInicial) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		if (fechaInicial) {
			cal.setTime(fecha);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			// cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 30);
			return sdf.format(cal.getTime());

		}
		return sdf.format(fecha);
	}
}
