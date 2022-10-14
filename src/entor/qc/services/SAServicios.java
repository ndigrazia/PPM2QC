package entor.qc.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import entor.qc.dto.QcDomain;
import entor.qc.dto.QcProyect;
import entor.qc.exception.QcException;

import entor.qc.symbols.QcSymbols;

public class SAServicios {

    /*private static final String url = "http://localhost:8080/qcbin";
    private static final String user = "Infra";
    private static final String password = "infra";
    private static final String dominio = "CERTIFICACION";//"PRUEBAS";
    private static final String proyecto = "2013";//"Liviano";*/
    
    private static List<String> DOMAINS_RESTRICTED = null;
    
    private SAConnection connection;
    
    static {
    	String[] domains_Forbidden = {"CALCULOS", "PRUEBAS", "DEFAULT", "LABORATORIO_TASA"};
    	DOMAINS_RESTRICTED = Arrays.asList(domains_Forbidden);
    }
    
	public SAServicios(SAConnection con) {
		connection = con;
	}
	
	/*public static void main(String[] args) {
    	
    	try {
    		SAConnection sa = SAConnectionFactory.createConnection(url);
    		sa.connect(user, password);
    		
    		SAServicios s = new SAServicios(sa);
    		
    		//s.createUser("tarsita2","fullname","email","phone","Descripcion",
    		//		"uniqueIdentifier=11111,ou=people​,dc=Enterprise,dc=ORGINIZATION_NAME,dc=com");
    		//s.AddUserToProject(dominio, proyecto, "tarsita2");
    		//s.AddUsersToMember(dominio, proyecto, "Desarrollo", "prueba");
    		System.out.println(s.getDomainsWithProyect());
    		//s.GetAllDomainProjects();
    		sa.disconnect();
		} catch (QcException e) {
			e.printStackTrace();
		}
		
    }*/
    
	public void createUser(String usr, String fullname, String email, String phone, String desc, String domAuth ) {
    	Dispatch.call(getQcConnection(), "CreateUserEx",
    			usr, fullname, email, phone, desc, QcSymbols.PASS_EMPTY, domAuth);
    }
    
    private ActiveXComponent getQcConnection() {
       
	   if (connection != null)
        	return connection.getConnection();
        else
        	throw new RuntimeException("No connection to Quality Center");
	   
    }
     
    public List<String> getAllDomains() throws QcException {
	
	  List<String> tmp = new ArrayList<String>();
	  
	  String xml = getQcConnection().invoke("GetAllDomains").getString();
	 
	  try {
		  Document document  = DocumentHelper.parseText(xml);
		  Element root = document.getRootElement();
		 
		  for(Iterator i = root.elementIterator(); i.hasNext();) {
			  Element element = (Element) i.next();
			 
			  QcDomain domain = getDomain(element);
			  if(!DOMAINS_RESTRICTED.contains(domain.getName().toUpperCase())) {
				  tmp.add(domain.getName());
			  }
		  }
		  
		  return tmp;
	  } catch (DocumentException e) {
		  throw new QcException("Error to process the respose xml.", e);
	  }
	
    }
  
    private QcDomain getDomain(Element pDomainTag) {
	  
    	QcDomain domain = new QcDomain();
    	
	  for(Iterator x = pDomainTag.elementIterator(); x.hasNext();) {
		  Element attribute = (Element) x.next();
		  
		  if(attribute.getName().equals(QcSymbols.DOMAIN_NAME_TAG)) {
			  domain.setName((String) attribute.getData());
			  break;
		  }
		  
	  }
	  
	  return domain;
	  
    }
  
    private QcProyect getProject(Element pProjectTag) {
  	  
    	QcProyect proyect = new QcProyect();
  	  
  	  	for(Iterator x = pProjectTag.elementIterator(); x.hasNext();) {
  	  		Element attribute = (Element) x.next();
  		  
  	  		if(attribute.getName().equals(QcSymbols.PROJECT_NAME_TAG)) {
  	  			proyect.setName((String)attribute.getData());
  	  		}
  		  
  	  		if(attribute.getName().equals(QcSymbols.PROJECT_IS_ACTIVE_TAG)) {
  	  			proyect.setActive(((String)attribute.getData()).equalsIgnoreCase("N")?false:true);
  	  		}
  	  	}
  	  
  	  	return proyect;
  	  
    }
  
    public List<String> getAllProjectsInDomain(String pDomain) throws QcException {
	 
    	List<String> tmp = new ArrayList<String>();
	 
    	String xml = getQcConnection().invoke("GetAllDomainProjects", new Variant(pDomain)).getString();
	 
    	try {
    		Document document  = DocumentHelper.parseText(xml);
    		Element root = document.getRootElement();
		 
    		for(Iterator i = root.elementIterator(); i.hasNext();) {
    			Element element = (Element) i.next();
			 
    			QcProyect p = getProject(element);
			 
    			if(p.isActive())
    				tmp.add(p.getName());
    		}
		 
    		return tmp;
    	} catch (DocumentException e) {
    		throw new QcException("Error to process the respose xml.", e);
    	}
	
    }
  
    public Map<String, List<String>> getDomainsWithProyect() throws QcException {

    	Map<String, List<String>> domainsProyect = new HashMap<String, List<String>>();
    	
    	List<String> domains = getAllDomains();
		for(String domain : domains) {
    		domainsProyect.put(domain, getAllProjectsInDomain(domain));
    	}
		
    	return domainsProyect;
    	
    }
    
}