package com.descarga.cfdi.util;

import java.math.BigInteger;
import java.security.Principal;
import java.util.UUID;

import javax.security.auth.x500.X500Principal;

import org.springframework.stereotype.Service;

@Service
public class MessagesSat implements IMessagesSat {

	@Override
	public String getMessageDigestValueAuthenticacion(String fechaInicial, String fechaFinal) {
		String messageDigestValue = "<u:Timestamp xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\" u:Id=\"_0\"><u:Created>"
				+ fechaInicial + "</u:Created><u:Expires>" + fechaFinal + "</u:Expires></u:Timestamp>";
		return messageDigestValue;
	}

	@Override
	public String getMessageSignatureValueAuthenticacion(String digesValue) {
		String messageNodo = "<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></CanonicalizationMethod><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\">"
				+ "</SignatureMethod><Reference URI=\"#_0\"><Transforms><Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></Transform></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></DigestMethod><DigestValue>"
				+ digesValue + "</DigestValue></Reference></SignedInfo>";
		return messageNodo;
	}

	@Override
	public String getMessageAuthenticacion(UUID uuid, String fechaInicial, String fechaFinal, String digesvalue,
			String signatureValue, String encodeCer) {
		StringBuffer xml = new StringBuffer();
		xml.append(
				"<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:u=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">"
						+ "<s:Header>"
						+ "<o:Security s:mustUnderstand=\"1\" xmlns:o=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">"
						+ "<u:Timestamp u:Id=\"_0\">" + "<u:Created>" + fechaInicial + "</u:Created>" + "<u:Expires>"
						+ fechaFinal + "</u:Expires>" + "</u:Timestamp>"
						+ "<o:BinarySecurityToken EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\" ValueType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3\" u:Id=\"uuid-"
						+ uuid.toString() + "-1\">" + encodeCer + "</o:BinarySecurityToken>"
						+ "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" + "<SignedInfo>"
						+ "<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>"
						+ "<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>"
						+ "<Reference URI=\"#_0\">" + "<Transforms>"
						+ "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" + "</Transforms>"
						+ "<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>" + "<DigestValue>"
						+ digesvalue + "</DigestValue>" + "</Reference>" + "</SignedInfo>" + "<SignatureValue>"
						+ signatureValue + "</SignatureValue>" + "<KeyInfo>" + "<o:SecurityTokenReference>"
						+ "<o:Reference URI=\"#uuid-" + uuid.toString()
						+ "-1\" ValueType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3\"/>"
						+ "</o:SecurityTokenReference>" + "</KeyInfo>" + "</Signature>\r\n" + "</o:Security>"
						+ "</s:Header>" + "<s:Body>" + "<Autentica xmlns=\"http://DescargaMasivaTerceros.gob.mx\"/>"
						+ "</s:Body>" + "</s:Envelope>");
		return xml.toString();
	}

	@Override
	public String getMessageSolicitud(String fechaInicial,String fechaFinal,String rfcEmisor,String tipoSolicitud,String certificado
			,Principal datosCertificado,BigInteger numCertificado,String digestValue,String signatureValue) {
		StringBuffer xml = new StringBuffer();
		xml.append(
				"<s:Envelope xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xd=\"http://www.w3.org/2000/09/xmldsig#\">"
						+ "<s:Header/>" + "<s:Body>" + "<des:SolicitaDescarga>"
						+ "<des:solicitud FechaFinal=\""+fechaFinal+"\" FechaInicial=\""+fechaInicial+"\" RfcEmisor=\""+rfcEmisor+"\" RfcSolicitante=\""+rfcEmisor+"\" TipoSolicitud=\""+tipoSolicitud+"\">"
						+ "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">"
						+ "<SignedInfo>"
						+ "<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>"
						+ "<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>"
						+ "<Reference URI=\"#_0\">"
						+ "<Transforms>"
						+ "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>"
						+ "</Transforms>"
						+ "<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>"
						+ "<DigestValue>"+digestValue+"</DigestValue>"
						+ "</Reference>" + "</SignedInfo>"
						+ "<SignatureValue>"+signatureValue+"</SignatureValue>"
						+ "<KeyInfo>" + "<X509Data>"
						+ "<X509IssuerSerial>"
						+ "<X509IssuerName>"+datosCertificado+"</X509IssuerName>"
						+ "<X509SerialNumber>"+numCertificado+"</X509SerialNumber>"
						+ "</X509IssuerSerial>"
						+ "<X509Certificate>"+certificado+"</X509Certificate>"
						+ "</X509Data>" + "</KeyInfo>"
						+ "</Signature>" + "</des:solicitud>"
						+ "</des:SolicitaDescarga>" + "</s:Body>" + "</s:Envelope>");
		return xml.toString();
	}
	
	@Override
	public String getMessageDigestValueSolicitud(String rfcEmisor,String fechaInicial,String fechaFinal,String tipoSolictud) {
			String messageDigestValue = "<des:SolicitaDescarga xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\">"
					+ "<des:solicitud RfcEmisor=\""+rfcEmisor+"\""
					+ "RfcSolicitante=\""+rfcEmisor+"\" FechaInicial=\""+fechaInicial+"\" FechaFinal=\""+fechaFinal+"\""
					+ " TipoSolicitud=\""+tipoSolictud+"\"></des:solicitud></des:SolicitaDescarga>";
		return messageDigestValue;
	}
	
	@Override
	public String getMessageSignatureValueSolicitud(String digesValue) {
			String messageSignatureValue = "<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\">"
					+ "</CanonicalizationMethod><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></SignatureMethod><Reference URI=\"\"><Transforms>"
					+ "<Transform Algorithm=\"http://www.w3.org/2000/09/xmldsig#enveloped-signature\"></Transform></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\">"
					+ "</DigestMethod><DigestValue>"+digesValue+"</DigestValue></Reference></SignedInfo>";
		return messageSignatureValue;
	}
	
	@Override
	public String getMessageVerificacion(String idSolicitud,String rfcEmisor,String certificado
			,Principal datosCertificado,BigInteger numCertificado,String digestValue,String signatureValue) {
		String messageVerificaccion="<s:Envelope xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xd=\"http://www.w3.org/2000/09/xmldsig#\">" + 
				"<s:Header/>" + 
				"<s:Body>" + 
				"<des:VerificaSolicitudDescarga>" + 
				"<des:solicitud IdSolicitud=\""+idSolicitud+"\" RfcSolicitante=\""+rfcEmisor+"\">" + 
				"<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" + 
				"<SignedInfo>" + 
				"<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" + 
				"<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>" + 
				"<Reference URI=\"\">" + 
				"<Transforms>" + 
				"<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" + 
				"</Transforms>" + 
				"<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>" + 
				"<DigestValue>"+digestValue+"</DigestValue>" + 
				"</Reference>" + 
				"</SignedInfo>" + 
				"<SignatureValue>"+signatureValue+"</SignatureValue>" + 
				"<KeyInfo>" + 
				"<X509Data>" + 
				"<X509IssuerSerial>" + 
				"<X509IssuerName>"+datosCertificado+"</X509IssuerName>" + 
				"<X509SerialNumber>"+numCertificado+"</X509SerialNumber>" + 
				"</X509IssuerSerial>" + 
				"<X509Certificate>"+certificado+"</X509Certificate>" + 
				"</X509Data>" + 
				"</KeyInfo>" + 
				"</Signature>" + 
				"</des:solicitud>" + 
				"</des:VerificaSolicitudDescarga>" + 
				"</s:Body>" + 
				"</s:Envelope>";
		
		return messageVerificaccion;
	}
	
	@Override
	public String getMessageDigestValueVerificacion(String idSolicitud,String rfcEmisor) {
		String messageDigestValue = "<des:VerificaSolicitudDescarga xmlns:des=\"http://descargamasivaterceros.sat.gob.mx\">" + 
				"<des:solicitud IdSolicitud=\""+idSolicitud+"\" RfcSolicitante=\""+rfcEmisor+"\">" + 
				"</des:solicitud>" + 
				"</des:VerificaSolicitudDescarga>";
		return messageDigestValue;
	}
	
	@Override
	public String getMessageSignatureValueVerificacion(String digestValue) {
		String messageSignatureValue = "<signedinfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><canonicalizationmethod algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">" + 
				"</canonicalizationmethod><signaturemethod algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></signaturemethod><reference uri=\"\">" + 
				"<transforms><transform algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></transform></transforms>" + 
				"<digestmethod algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"></digestmethod><digestvalue>"+digestValue+"</digestvalue>" + 
				"</reference></signedinfo>";
		return messageSignatureValue;
	}
	
	@Override
	public String getMessageDescarga(String idPaquete,String rfcEmisor,String certificado
			,Principal datosCertificado,BigInteger numCertificado,String digestValue,String signatureValue) {
		
		String messageDescarga = "<s:Envelope xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xd=\"http://www.w3.org/2000/09/xmldsig#\">" + 
				"<s:Header/>" + 
				"<s:Body>" + 
				"<des:PeticionDescargaMasivaTercerosEntrada>" + 
				"<des:peticionDescarga IdPaquete=\""+idPaquete+"\" RfcSolicitante=\""+rfcEmisor+"\">" + 
				"<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">" + 
				"<SignedInfo>" + 
				"<CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" + 
				"<SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>" + 
				"<Reference URI=\"\">" + 
				"<Transforms>" + 
				"<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"/>" + 
				"</Transforms>" + 
				"<DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>" + 
				"<DigestValue>"+digestValue+"</DigestValue>" + 
				"</Reference>" + 
				"</SignedInfo>" + 
				"<SignatureValue>"+signatureValue+"</SignatureValue>" + 
				"<KeyInfo>" + 
				"<X509Data>" + 
				"<X509IssuerSerial>" + 
				"<X509IssuerName>"+datosCertificado+"</X509IssuerName>" + 
				"<X509SerialNumber>"+numCertificado+"</X509SerialNumber>" + 
				"</X509IssuerSerial>" + 
				"<X509Certificate>"+certificado+"</X509Certificate>" + 
				"</X509Data>" + 
				"</KeyInfo>" + 
				"</Signature>" + 
				"</des:peticionDescarga>" + 
				"</des:PeticionDescargaMasivaTercerosEntrada>" + 
				"</s:Body>" + 
				"</s:Envelope>";
		return messageDescarga;
	}
	
	@Override
	public String getDigestValueDescarga(String idPaquete,String rfcEmisor ) {
		String messageDigestValue = "<des:PeticionDescargaMasivaTercerosEntrada xmlns:des=\"http://DescargaMasivaTerceros.sat.gob.mx\">"
				+ "<des:peticionDescarga IdPaquete=\""+idPaquete+"\" RfcSolicitante=\""+rfcEmisor+"\"></des:peticionDescarga>"
				+ "</des:PeticionDescargaMasivaTercerosEntrada>";
		
		return messageDigestValue;
	}
	
	@Override
	public String getSignatureValueDescarga(String digestValue) {
		String messageSignatureValue = "<SignedInfo xmlns=\"http://www.w3.org/2000/09/xmldsig#\"><CanonicalizationMethod Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\">"
				+ "</CanonicalizationMethod><SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"></SignatureMethod><Reference URI=\"\"><Transforms>"
				+ "<Transform Algorithm=\"http://www.w3.org/2001/10/xml-exc-c14n#\"></Transform></Transforms><DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\">"
				+ "</DigestMethod><DigestValue>"+digestValue+"</DigestValue></Reference></SignedInfo>";
				
				return messageSignatureValue;
	}
}
