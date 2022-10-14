package entor.qc.ppm.jaxws;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import entor.qc.exception.QcException;
import entor.qc.ppm.exception.QcPpmWebServicesFault;
import entor.qc.ppm.exception.QcPpmWebServicesException;
import entor.qc.ppm.services.QcPpmApiServicesImp;
import entor.qc.ppm.services.QcPpmApiServicesItf;
import entor.qc.services.QcConnection;
import entor.qc.services.QcConnectionFactory;

@WebService
@SOAPBinding(style = Style.RPC)
public class QcPpmWebServices {

	private static final String url = "http://localhost:8080/qcbin"; //DESARROLLO
	//private static final String url = "http://10.204.128.56:8080/qcbin";//PRODUCCION
	private static final String user = "user";
    private static final String password = "pass";
    
    private static final String dominio = "CAPACITACION"; //DESARROLLO
    private static final String proyecto = "2014"; //DESARROLLO
    
    //private static final String dominio = "PRUEBAS"; //PRODUCCION
    //private static final String proyecto = "Liviano"; //PRODUCCION
	
	private QcPpmApiServicesItf services = null;
	
	private QcConnection qc = null;
	
	public QcPpmWebServices() {
	}
	
	@PostConstruct
	@WebMethod(exclude = true)
	public void createServiceQC() {
		
		try {
			if (services == null){
				qc = QcConnectionFactory.createConnection(url);
				qc.connect(user, password, dominio, proyecto);
				services = new QcPpmApiServicesImp(qc);
			}
		} catch (QcException e) {
			e.printStackTrace();
		}
		
	}
	
	@PreDestroy
	@WebMethod(exclude = true)
	public void releaseServiceQC() {
		
		try {
			if (qc != null){
				qc.disconnect(); 
				qc = null;
			}
		} catch (QcException e) {
			e.printStackTrace();
		}
		
	}
	
	@WebMethod(operationName = "addProyectToInboxFromPPM")	
	@WebResult(name="nroReleaseQC")
	public int addProyectToInboxFromPPM(@WebParam(name = "nroProyecto") String nroProyecto,
			@WebParam(name = "tituloProyecto") String tituloProyecto, 
			@WebParam(name = "tipoProyecto") String tipoProyecto, 
			@WebParam(name = "nroOt") String nroOt, 
			@WebParam(name = "tituloOt") String tituloOt, 
			@WebParam(name = "fechaInicioPlanificada") Date fechaInicioPlanificada, 
			@WebParam(name = "fechaFinalPlanificada") Date fechaFinalPlanificada, 
			@WebParam(name = "fechaComprometida") Date fechaComprometida, 
			@WebParam(name = "fechaCreacionOtRelease") Date fechaCreacionProyecto,
			@WebParam(name = "analistaTestingProyecto") String analistaTestingProyecto,
			@WebParam(name = "analistaTestingOt") String analistaTestingOt,
			@WebParam(name = "estadoOt") String estadoOt,
			@WebParam(name = "jefeProyecto") String jefeProyecto,
			@WebParam(name = "responsableProyecto") String responsableProyecto,
			@WebParam(name = "responsableOt") String responsableOt,
			@WebParam(name = "negocio") String negocio
	) throws QcPpmWebServicesFault {
		
		try {
			return services.addProyectToInboxFromPPM(nroProyecto, tituloProyecto, 
					tipoProyecto, nroOt, tituloOt, fechaInicioPlanificada, 
					fechaFinalPlanificada, fechaComprometida);
		} catch(Throwable e) {
			QcPpmWebServicesException fault = new QcPpmWebServicesException();
			fault.setMessage(e.getMessage());
			
			throw new QcPpmWebServicesFault(fault);
		}
		
	}
	
	/*public static void main(String[] args) {
		
		QcPpmWebServices services = new QcPpmWebServices();
			
		services.createServiceQC();
			
		try {
			System.out.println(services.addProyectToInboxFromPPM("PRY777", "Integración con los sistemas satélites", "OT Externa", "77632", "Certificación proyecto (Servicio)", null, null, null, null, null, null, null, null, null, null, null));
			System.out.println(services.addProyectToInboxFromPPM("PRY71511", "Prueba de los sistemas satélites", "OT Externa", "77633", "Certificación proyecto (Servicio)", null, null, null, null, null, null, null, null, null, null, null));
			System.out.println(services.addProyectToInboxFromPPM("PRY71511", "Sistemas satélites", "OT Externa" , "77634", "Certificación proyecto (Servicio)", null, null, null, null, null, null, null, null, null, null, null));
			System.out.println(services.addProyectToInboxFromPPM("PRY71511", "Integración de FACT con los sistemas satélites", "OT Externa" , "77636", "Certificación proyecto (Servicio)", null, null, null, null, null, null, null, null, null, null, null));
		} catch (QcPpmWebServicesFault e) {
			e.printStackTrace();
		}
			  
		services.releaseServiceQC();
		
	}*/
	
}
