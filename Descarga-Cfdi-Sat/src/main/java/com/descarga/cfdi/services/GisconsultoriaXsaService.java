package com.descarga.cfdi.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.descarga.cfdi.model.Clientes;
import com.descarga.cfdi.service.IClienteService;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


@Service
public class GisconsultoriaXsaService {

	protected final Logger LOG = Logger.getLogger(GisconsultoriaXsaService.class);

    /*@Value("${path.archivo.archivosXml}")
    private String pathArchivos;

    @Autowired
    private IClienteService empresaService;
    
    @Autowired
	private IDescargaCfdi descargaCfdi;

    public void ObtenerArchivos(Long sucursalId, Date fecha,Date fechaActual) throws IOException{

        Clientes sucursal = empresaService.findById(sucursalId);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cFechaInicial = Calendar.getInstance();
        cFechaInicial.setTime(fecha);
        cFechaInicial.add(Calendar.DAY_OF_YEAR, -3);
        String fechaInicial = dateFormat.format(cFechaInicial.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaActual);
        String fechaFinal = dateFormat.format(calendar.getTime());
        int pageZise = 100;
		int totalCfdiEmitidos=0;
		File zip = null;
        //calendar.setTime(fecha);
        //calendar.add(Calendar.DAY_OF_MONTH, 1);
        
        if(sucursal.getKeyXsa() != null && sucursal.getServidor() != null) {
        try {
        	// get TotalCfdiEmitidos
        		totalCfdiEmitidos = descargaCfdi.getTotalCfdiGenerados(sucursal.getServidor(), sucursal.getKeyXsa(),
        		fechaInicial, fechaFinal);
        		if(totalCfdiEmitidos==0) {
        				LOG.info("No se encontraron cfdi emitidos de la sucursal: "+sucursal.getNombre()+" con fecha: "+fechaInicial);
        				throw new Exception("No se encontraron cfdi emitidos de la sucursal: "+sucursal.getNombre()+" con fecha: "+fechaInicial);
        		}
        	// totalPageNumber
        		int totalPagesNumber = (int) (Math.floor(totalCfdiEmitidos / pageZise));
        	// get CFDI por pageNumber
    			LOG.info("Total de CFDI a descargar: " +totalCfdiEmitidos);
    			for (int x = 0; x <= totalPagesNumber; x++) {
    				InputStream zipStream = descargaCfdi.getCfdiZip(fechaInicial, fechaFinal, sucursal.getRfc(),pageZise,x);
    				
    				zip = File.createTempFile(sucursal.getRfc(),
    						"_P".concat(String.valueOf(x) + "_").concat(fechaInicial).concat(".zip"));
    				OutputStream out = new BufferedOutputStream(new FileOutputStream(zip));
    				copyInputStream(zipStream, out);
    				out.close();

    				File path = new File(pathArchivos);
    				LOG.info("Obteniendo archivos para la sucursal: " + sucursal.getNombre());
    				if (zip.length() > 0) {
    					unpackArchive(zip, path,sucursal.getNombre());
    				}
    			}
    			 empresaService.updateFechaInicialClienteById(calendar.getTime(),sucursal.getId() );
        

        }catch (IOException e) {
			e.printStackTrace();
			LOG.error("Error al momento de transformar la url : "+e);
		} catch (Exception e) {
			e.printStackTrace();
		}
        }else {
        	LOG.info("No se encuentra un servidor y key asignado al cliente:" +sucursal.getNombre());
        }
    }*/

    public void unpackArchive(File zip, File path, String nombreCliente) throws IOException{
    	File pathCliente = new  File(path.toString()+File.separator+nombreCliente);
    	System.out.println(zip);
        if(!zip.exists()){
            throw new IOException(zip.getAbsolutePath() + " no existe el archivo");
        }
       
        ZipFile zipFile = new ZipFile(zip);
        for(Enumeration entries = zipFile.entries(); entries.hasMoreElements();){
            ZipEntry entry = (ZipEntry)entries.nextElement();

            if(entry.getName().contains(".xml") || entry.getName().contains(".pdf") || 
            		entry.getName().toLowerCase().contains(".xml") || entry.getName().toLowerCase().contains(".pdf")){
                LOG.info("Obteniendo el archivo: " + entry.getName());
                File file = new File(pathCliente, File.separator + entry.getName());
                if(!buildDirectory(file.getParentFile())){
                    throw new IOException("No se puede crear el directorio: " + file.getParentFile());
                }
                if(!entry.isDirectory()){
                    copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream(file)));
                }else{
                    if(!buildDirectory(file)){
                        throw new IOException("No se puede crear el directorio: " + file);
                    }
                }
            }
        }

        zipFile.close();

    }

    public void copyInputStream(InputStream in, OutputStream out) throws IOException{
        byte[] buffer = new byte[2048];

        int len = in.read(buffer);
        while(len >= 0){
            out.write(buffer, 0, len);
            len = in.read(buffer);
        }

        in.close();
        out.close();
    }

    private boolean buildDirectory(File file) {
        return file.exists() || file.mkdir();
    }
}
