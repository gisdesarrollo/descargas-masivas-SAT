package com.descarga.cfdi.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.descarga.cfdi.model.Clientes;
import com.descarga.cfdi.service.IClienteService;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

//@Service
public class DescargaCfdi{
	
	/*protected final Logger LOG = Logger.getLogger(DescargaCfdi.class.getName());
    @Autowired
    private IClienteService clienteService;

    @Value("${api.xsa.url.descarga.cfdi}")
	private String path;
	
	@Value("${api.xsa.url.consulta.generados.cfdi}")
	private String pathCfdiGenerados;
	
	@Override
	public InputStream getCfdiZip(String fechaInicial, String fechaFinal,String rfc,int pageZise,int pageNumber) {

		InputStream in = null;
		try {
			Clientes sucursal = clienteService.getClienteByRfc(rfc);

			if (sucursal == null) {
				throw new Exception("Error: Sucursal no encontrada");		
			}
				
		    URL url = new URL("https://".concat(sucursal.getServidor()).concat(":9050").concat("/").concat(sucursal.getKeyXsa()).concat(path)
						.concat("?representacion=XML_PDF").concat("&fechaInicial=").concat(fechaInicial).concat("&fechaFinal=").concat(fechaFinal)
						.concat("&pageSize="+pageZise).concat("&pageNumber="+pageNumber));

		    in = new BufferedInputStream(url.openStream(), 1024);
		    			
		}catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error: al momento  de ejecución: "+e);
		}
		return in;
	}*/
	
	/*@Override
	public int getTotalCfdiGenerados(String servidor,String keyXsa,String fechaInicial,String fechaFinal) {
		
		String url = "https://".concat(servidor).concat(":9050/").concat(keyXsa).concat(pathCfdiGenerados)
				.concat("?fechaInicial="+fechaInicial).concat("&fechaFinal=" +fechaFinal);
			try {
				URLConnection connection = new URL(url).openConnection();
				InputStream response = connection.getInputStream();
				Scanner scanner = new Scanner(response);
				if(scanner!=null) {
					int responseBody = Integer.parseInt(scanner.useDelimiter("\\A").next());
					return responseBody;
				}
			}catch (MalformedURLException  e) {
				e.printStackTrace();
				LOG.error("Error en formación de la URL ");
			} catch (IOException e) {
				e.printStackTrace();
				LOG.error("Error al momento de ejecución");
			}
		return 0;
		
	}*/


}
