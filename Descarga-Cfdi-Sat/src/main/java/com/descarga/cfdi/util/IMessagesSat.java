package com.descarga.cfdi.util;

import java.math.BigInteger;
import java.security.Principal;
import java.util.UUID;

public interface IMessagesSat  {

	public String getMessageDigestValueAuthenticacion(String fechaInicial, String fechaFinal);

	public String getMessageSignatureValueAuthenticacion(String digesValue);

	public String getMessageAuthenticacion(UUID uuid, String fechaInicial, String fechaFinal, String digesvalue,
			String signatureValue, String encodeCer);

	public String getMessageDigestValueSolicitud(String rfcEmisor, String fechaInicial, String fechaFinal,
			String tipoSolictud);

	public String getMessageSignatureValueSolicitud(String digesValue);


	public String getMessageSolicitud(String fechaInicial, String fechaFinal, String rfcEmisor, String tipoSolicitud,
			String certificado, Principal datosCertificado, BigInteger numCertificado, String digestValue,
			String signatureValue);

	public String getMessageVerificacion(String idSolicitud, String rfcEmisor, String certificado, Principal datosCertificado,
			BigInteger numCertificado, String digestValue, String signatureValue);

	public String getMessageDigestValueVerificacion(String idSolicitud, String rfcEmisor);

	public String getMessageSignatureValueVerificacion(String digestValue);

	public String getMessageDescarga(String idPaquete, String rfcEmisor, String certificado, Principal datosCertificado,
			BigInteger numCertificado, String digestValue, String signatureValue);

	public String getDigestValueDescarga(String idPaquete, String rfcEmisor);

	public String getSignatureValueDescarga(String digestValue);

}
