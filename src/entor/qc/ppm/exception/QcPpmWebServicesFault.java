package entor.qc.ppm.exception;

import javax.xml.ws.WebFault;

@WebFault(name="QcPpmWebServicesFault")
public class QcPpmWebServicesFault extends Exception {

	private QcPpmWebServicesException fault;

	public QcPpmWebServicesFault() {
	}

	public QcPpmWebServicesFault(QcPpmWebServicesException fault) {
		super(fault.getMessage()); 
        this.fault = fault;
	}
	
	public QcPpmWebServicesFault(String message, QcPpmWebServicesException faultInfo){
		super(message);
		fault = faultInfo;
	}
	
	public QcPpmWebServicesFault(String message, QcPpmWebServicesException faultInfo, Throwable cause){
		super(message,cause);
		fault = faultInfo;
	}

	public QcPpmWebServicesException getFaultInfo(){
		return fault;
	}
	
	public QcPpmWebServicesFault(String message) {
		super(message);
	}
	
	public QcPpmWebServicesFault(Throwable cause) {
		super(cause);
	}

	public QcPpmWebServicesFault(String message, Throwable cause) {
		super(message, cause);
	}

}
