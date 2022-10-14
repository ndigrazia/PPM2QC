package entor.qc.ppm.services;

import java.util.Date;

public interface QcPpmApiServicesItf {

	public int addProyectToInboxFromPPM(String nroProyecto, String tituloProyecto, String tipoProyecto, String nroOt, String tituloOt, Date inicioFecha, 
			Date finalFecha, Date fechaComprometida);

}
