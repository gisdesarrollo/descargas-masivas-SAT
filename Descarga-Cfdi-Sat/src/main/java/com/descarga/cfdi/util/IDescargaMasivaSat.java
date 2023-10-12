package com.descarga.cfdi.util;

import java.util.UUID;

public interface IDescargaMasivaSat {

	public String getAuthenticacion(UUID uuid, String fechaInicial, String fechaFinal, byte[] cerByte, byte[] keyByte,
			String password);


	public String getSolicitud(String fechaInicial, String fechaFinal, String rfcEmisor, String tipoSolicitud, byte[] cerByte,
			byte[] keyByte, String password);


	public String getverificacion(String rfcEmisor, String idSolicitud, byte[] cerByte, byte[] keyByte, String password);


	public String getDescarga(String rfcEmisor, String idPaquete, byte[] cerByte, byte[] keyByte, String password);


	public void decodeZip(String zipData, String rfc);
}
