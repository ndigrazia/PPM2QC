package entor.qc.ppm.services;

import java.util.Date;

import entor.qc.services.QcConnection;
import entor.qc.services.ReleasesServicios;

public class QcPpmApiServicesImp implements QcPpmApiServicesItf {

	private ReleasesServicios releaseServices;
	
	public QcPpmApiServicesImp(QcConnection con) {
		releaseServices = new ReleasesServicios(con);
	}

	@Override
	public int addProyectToInboxFromPPM(String nroProyecto,
			String tituloProyecto, String tipoProyecto, String nroOt, String tituloOt, Date inicioFecha, 
			Date finalFecha, Date fechaComprometida) {
		return releaseServices.addProyectToInboxFromPPM(nroProyecto, tituloProyecto, tipoProyecto, nroOt, tituloOt,
				inicioFecha, finalFecha, fechaComprometida);
	}

}
