package entor.qc.ppm.client;

import javax.xml.namespace.QName; 
import javax.xml.ws.soap.SOAPBinding; 
import javax.xml.ws.Dispatch; 
import javax.xml.ws.Service;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

public class QcPpmWebServicesClient {

	private static final String WSDL_ENDPOINT_URL="http://localhost:8080/PPM2QC/services"; 

	public static void main(String[] args) {
		try {
			  QName serviceName = new QName("http://jaxws.ppm.qc.entor","QcPpmWebServicesService");             
			  QName portQName = new QName("http://jaxws.ppm.qc.entor","QcPpmWebServicesPort");
			  
			  Service service = Service.create(serviceName);
			  service.addPort(portQName, SOAPBinding.SOAP11HTTP_BINDING, WSDL_ENDPOINT_URL);

			  Dispatch<SOAPMessage> dispatch = service.createDispatch(portQName, SOAPMessage.class, Service.Mode.MESSAGE);

			  MessageFactory mf = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

			  SOAPMessage request = mf.createMessage();
			  SOAPPart part = request.getSOAPPart();

			  SOAPEnvelope env = part.getEnvelope();
			  SOAPBody body = env.getBody();

			  SOAPElement operation = body.addChildElement("addProyectToInboxFromPPM", "ns0",
			  "http://jaxws.ppm.qc.entor/");
			 
			  SOAPElement value = operation.addChildElement("nroProyecto");
			  value.addTextNode("PRY71514");
			  
			  value = operation.addChildElement("tituloProyecto");
			  value.addTextNode("Integracion con los sistemas satelites");
			  
			  value = operation.addChildElement("tipoProyecto");
			  value.addTextNode("OT Externa");
			  
			  value = operation.addChildElement("nroOt");
			  value.addTextNode("77632");
			  
			  value = operation.addChildElement("tituloOt");
			  value.addTextNode("Certificacion proyecto (Servicio )");
			  
			  request.saveChanges();

			  SOAPMessage response = dispatch.invoke(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
