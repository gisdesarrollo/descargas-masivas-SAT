package com.descarga.cfdi.services;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.ssl.PKCS8Key;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.descarga.cfdi.model.Solicitudes;
import com.descarga.cfdi.service.ISolicitudService;

@Service
public class GetCfdiSatService {

	protected final Logger LOG = Logger.getLogger(GetCfdiSatService.class);

	@Autowired
	private ISolicitudService solicitudService;
	
	public String connectionSat(String urlWsSat, String SOAPAction, String xmlData, String token,
			String tipoSolicitud) {
		HttpURLConnection urlConnection = null;
		String result = null;
		int status = 0;
		String response = null;
		String idSolicitud = null;
		//int totalCfdi = 0;
		//int estadoSolicitud = 0;
		//int codigoEstadoSolicitud = 0;
		
		try {
			LOG.info("ESTABLECIENDO CONEXION CON WS SAT..");
			byte[] xmlByteData = xmlData.getBytes("UTF-8");

			URL link = new URL(urlWsSat);
			URLConnection connection = link.openConnection();
			urlConnection = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xmlByteData);
			byte[] b = bout.toByteArray();

			urlConnection.setRequestProperty("Content-Length", String.valueOf(b.length));
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
			urlConnection.setRequestProperty("Accept", "text/xml");
			urlConnection.setRequestProperty("Cache-Control", "no-cache");
			if(SOAPAction!=null) {
			urlConnection.setRequestProperty("SOAPAction", SOAPAction);
			}
			if (tipoSolicitud == "solicitud" && token != null || tipoSolicitud == "verificacion" && token != null ||
					tipoSolicitud=="descarga" && token!=null) {
				urlConnection.setRequestProperty("Authorization", "WRAP access_token=\"" + token + "\"");

			}
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			OutputStream out = urlConnection.getOutputStream();
			out.write(b);
			out.close();

			// Read the response.
			InputStreamReader isr = null;
			//System.out.println(urlConnection.getResponseMessage());
			LOG.info("ESTATUS DE CONEXION: "+urlConnection.getResponseCode());
			if (urlConnection.getResponseCode() == 200) {
				isr = new InputStreamReader(urlConnection.getInputStream());
			} else {
				isr = new InputStreamReader(urlConnection.getErrorStream());
				throw new Exception("Ocurrio un error en el response del web service estatus: "+urlConnection.getResponseCode());
			}
			
			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			String outputString = null;
			
			while ((responseString = in.readLine()) != null) {
				outputString = responseString;
			}
			
			// parse response
			if (tipoSolicitud == "authenticacion") {
				Document document = parseXml(outputString);
				NodeList nodeLst = document.getElementsByTagName("AutenticaResult");
				String elementValue = nodeLst.item(0).getTextContent();
				LOG.info("Token authenticación obtenido correctamente");
				result = elementValue;
			}
			Document document = parseXml(outputString);
			if (tipoSolicitud == "solicitud") {
				NodeList nodeList = document.getElementsByTagName("SolicitaDescargaResult");
				
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					Element element = (Element) node;
					if(node.getAttributes().getLength()>1) {
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							status = Integer.parseInt(element.getAttribute("CodEstatus"));
							response = element.getAttribute("Mensaje");
							idSolicitud = element.getAttribute("IdSolicitud");
						}
					}else {
						response = element.getAttribute("Mensaje");
					}
				}
				if (status != 5000) {
					LOG.error("Error en solicitud: " + status + " " + response);
					throw new Exception("Error: " + status + " " + response);
				}
				
				LOG.info("solicitud mensaje response: " + response);
				result = idSolicitud;
			}
			if(tipoSolicitud== "descarga") {
				NodeList nodeList = document.getElementsByTagName("h:respuesta");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
						Element element = (Element) node;
						status = Integer.parseInt(element.getAttribute("CodEstatus"));
						response = element.getAttribute("Mensaje");

					}
				}
				if (status != 5000) {
					LOG.error("Error en descarga: " + status + " " + response);
					throw new Exception("Error: " + status + " " + response);
				}
				NodeList nodeLst = document.getElementsByTagName("Paquete");
					String zip = nodeLst.item(0).getTextContent();
					LOG.info("Descarga mensaje response: " + response);
				
				result = zip;
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error al momento de la conexion del Web Service");
		}

		return result;

	}
	
	public List<String> connectSatWSVerificacion(String urlWsSat, String SOAPAction, String xmlData, String token,
			String tipoSolicitud,String idSolicitud,String rfcEmisor){
			
		HttpURLConnection urlConnection = null;
		List<String> result = new ArrayList<>();
		int status = 0;
		String response = null;
		int totalCfdi = 0;
		int estadoSolicitud = 0;
		int codigoEstadoSolicitud = 0;
		Solicitudes solicitud= new Solicitudes();
		try {

			byte[] xmlByteData = xmlData.getBytes("UTF-8");

			URL link = new URL(urlWsSat);
			URLConnection connection = link.openConnection();
			urlConnection = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			bout.write(xmlByteData);
			byte[] b = bout.toByteArray();

			urlConnection.setRequestProperty("Content-Length", String.valueOf(b.length));
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Content-type", "text/xml; charset=utf-8");
			urlConnection.setRequestProperty("Accept", "text/xml");
			urlConnection.setRequestProperty("Cache-Control", "no-cache");
			if(SOAPAction!=null) {
			urlConnection.setRequestProperty("SOAPAction", SOAPAction);
			}
			if (tipoSolicitud == "solicitud" && token != null || tipoSolicitud == "verificacion" && token != null ||
					tipoSolicitud=="descarga" && token!=null) {
				urlConnection.setRequestProperty("Authorization", "WRAP access_token=\"" + token + "\"");

			}
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			OutputStream out = urlConnection.getOutputStream();
			out.write(b);
			out.close();

			// Read the response.
			InputStreamReader isr = null;
			if (urlConnection.getResponseCode() == 200) {
				isr = new InputStreamReader(urlConnection.getInputStream());
			} else {
				isr = new InputStreamReader(urlConnection.getErrorStream());
				throw new Exception("Ocurrio un error en el response del web service estatus: "+urlConnection.getResponseCode());
			}

			BufferedReader in = new BufferedReader(isr);
			String responseString = null;
			String outputString = null;
			
			while ((responseString = in.readLine()) != null) {
				outputString = responseString;
			}
			// parse response
			Document document = parseXml(outputString);
			if (tipoSolicitud == "verificacion") {
				NodeList nodeList = document.getElementsByTagName("VerificaSolicitudDescargaResult");
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node node = nodeList.item(i);
					if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) node;
					if(node.getAttributes().getLength()>4) {
						status = Integer.parseInt(element.getAttribute("CodEstatus"));
						totalCfdi = Integer.parseInt(element.getAttribute("NumeroCFDIs"));
						estadoSolicitud = Integer.parseInt(element.getAttribute("EstadoSolicitud"));
						codigoEstadoSolicitud = Integer.parseInt(element.getAttribute("CodigoEstadoSolicitud"));
						response = element.getAttribute("Mensaje");
						
					
						}else if(node.getAttributes().getLength()>3){
							status = Integer.parseInt(element.getAttribute("CodEstatus"));
							totalCfdi = Integer.parseInt(element.getAttribute("NumeroCFDIs"));
							estadoSolicitud = Integer.parseInt(element.getAttribute("EstadoSolicitud"));
							response = element.getAttribute("Mensaje");
						}else {
							status = Integer.parseInt(element.getAttribute("CodEstatus"));
							estadoSolicitud = Integer.parseInt(element.getAttribute("EstadoSolicitud"));
							response = element.getAttribute("Mensaje");
						}
					}
				}
				if (status != 5000 && codigoEstadoSolicitud != 5000) {
					LOG.error("Error en verificación: "+status + " " + response);
					throw new Exception("Error en verificación: " + status + " " + response);
				}
				if(totalCfdi>0 && estadoSolicitud==3) {
					String idPaquete=null;
					NodeList nodeLst = document.getElementsByTagName("IdsPaquetes");
					LOG.info("verificación mensaje response: " + response);
					LOG.info("Total de CFDIs a descargar: " + totalCfdi);
				
					if(nodeLst.getLength()<2) {
						idPaquete = nodeLst.item(0).getTextContent();
						result.add(idPaquete);
					}else {
						for(int x=0; x<nodeLst.getLength(); x++) {
							idPaquete = nodeLst.item(x).getTextContent();
							result.add(idPaquete);
							}
					}
				}else {
					solicitud.setIdSolicitud(idSolicitud);
					solicitud.setEstatus(estadoSolicitud);
					solicitud.setRfcCliente(rfcEmisor);
					solicitud.setFecha(new Date());
					solicitud.setHora(new SimpleDateFormat("HH:mm:ss").format(new Date()));
					solicitudService.save(solicitud);
					LOG.info("Verificación mensaje response: "+response);
					LOG.info("Estado de solictud: "+estadoSolicitud);
					
				}
			}
						
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error al momento de la conexion del Web Service");
		}

		return result;
		
	}

	public String getDigesValue(String message) {
		// Codififca sha1 with base64
		String encodeDigesValue = Base64.getEncoder()
				.encodeToString(DigestUtils.sha1(message.getBytes(Charset.forName("UTF-8"))));
		return encodeDigesValue;
	}

	public String getSignatureValue(PrivateKey privateKey, String digesValue, String messageNodo)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException {
		byte[] nodoMessage = null;
		String encodeSignatureValue = null;
		try {
			nodoMessage = messageNodo.getBytes("UTF8");
			// algoritmo
			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initSign(privateKey);
			signer.update(nodoMessage);
			encodeSignatureValue = new String(Base64.getEncoder().encodeToString(signer.sign()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			LOG.error("Error al momento de ejecucion:" + e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return encodeSignatureValue;
	}

	public static PrivateKey getPrivateKey(byte[] encryptedKey, String passphrase) {
		try {
			PKCS8Key pkcs8 = new PKCS8Key(encryptedKey, passphrase.toCharArray());
			return pkcs8.getPrivateKey();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Clave del archivo privado (archivo key) inválida");
		}
	}

	public static X509Certificate getCertificate(byte[] cerByte) {
		InputStream in = null;
		X509Certificate cert = null;
		try {
			in = new ByteArrayInputStream(cerByte);
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			cert = (X509Certificate) cf.generateCertificate(in);

		} catch (CertificateException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
		}
		return cert;
	}

	public boolean verify(String message, String signature, Certificate certificate) {
		try {
			byte[] signatureByte = DatatypeConverter.parseBase64Binary(signature);
			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initVerify(certificate);
			signer.update(message.getBytes("UTF8"));
			return signer.verify(signatureByte);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Document parseXml(String in) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource(new StringReader(in));
			return db.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
