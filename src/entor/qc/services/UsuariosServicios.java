package entor.qc.services;

import java.util.ArrayList;
import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
//import java.util.Map;
//import java.util.Set;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class UsuariosServicios {

	/*//private static final String url = "http://10.204.128.56:8080/qcbin";
    private static final String url = "http://localhost:8080/qcbin";
    private static final String user = "Infra";
    private static final String password = "infra";
    private static final String dominio = "CERTIFICACION";//"PRUEBAS";
    private static final String proyecto = "2013";//"Liviano";*/
    
    private static List<String> MEMBER_RESTRICTED = null; 
	
    private QcConnection connection;
    
    static {
    	String[] members_Forbidden = {"DESARROLLO QC" , "DEVELOPER", "PROJECT MANAGER", "QATESTER", "SUPERUSER", "TDADMIN", "AFT A",
    			"AMBIENTES A", "DESARROLLO1", "ADMIN_NO_OTORGAR", "AMBIENTES_OLD"};
    	MEMBER_RESTRICTED = Arrays.asList(members_Forbidden);
    }
    
	public UsuariosServicios(QcConnection con) {
		connection = con;
	}
	 
	/*public static void main(String[] args) {
		
		try {
	    	  //Alta usuario al dominio y Grupo
	    	  QcConnection qc = QcConnectionFactory.createConnection(url);
	    	  qc.connect(user, password, dominio, proyecto);
	    	  
	    	  UsuariosServicios u = new UsuariosServicios(qc);
	    	   
	    	  //Asignar usuario a proyecto y grupo
	    	  //u.addUserToProyect("newSir2", "Desarrollo");
	    	  
	    	  //Perfiles
	    	  //String[] member = u.getMemberGroup("MOVIL", "2011");
	    	  //for(int x=0;x<member.length; x++)
	    	  //System.out.println(member[x]);
	    	  
	    	  //Dominios
	    	  //Map<String,String[]> domains = u.getDomainsWithProyect();
	    	  //Set<String> domain = domains.keySet();
	    	  //Iterator<String> ite = domain.iterator();
	    	  //while(ite.hasNext()) {
	    	  //System.out.println("-------------------------------------");
	    	  //String name = ite.next();
	    	  //System.out.println(name);
	    	  //System.out.println("-------------------------------------"); 
	    	  //String[] proyects = domains.get(name);
	    	  //for(int x=0;x<proyects.length; x++)
	    	  //  System.out.println(proyects[x]);
	    	  //}
	    	
	    	  //Existe el usuario
	    	  System.out.println(u.isExistingUser("Aa1rcuri"));
	    	  
	    	  qc.disconnect();
		}
	    catch (Exception e) {
		            e.printStackTrace();
	    }	
	}*/
	
	public boolean isExistingUser(String pUsername) {
		
		ActiveXComponent lUsers = connection.getConnection()
			.getPropertyAsComponent("customization").getPropertyAsComponent("users");
		
		return lUsers.invoke("UserExistsInSite", pUsername).getBoolean();
		
	}
	
    public void addUserToProyect(String pUser, String pMember) {
    	
     	ActiveXComponent connection = getQcConnection();
     	
    	ActiveXComponent customization = connection.getPropertyAsComponent("Customization");
		customization.invoke("Load");
		ActiveXComponent users = customization.getPropertyAsComponent("Users");
		
		Variant user = users.invoke("AddUser", new Variant(pUser));
		
		ActiveXComponent CustUsersGroups  = customization.getPropertyAsComponent("UsersGroups");
		ActiveXComponent CustGroup = CustUsersGroups.invokeGetComponent("Group", new Variant(pMember));
		CustGroup.invoke("AddUser", user);
		
		customization.invoke("Commit");
		
    }
    
	private ActiveXComponent getQcConnection() {
        if (connection != null)
        	return connection.getConnection();
        else
        	throw new RuntimeException("No connection to Quality Center");
    }
    
    public String[] getMemberGroup(String domain, String proyect) {
    	
    	ActiveXComponent connection = getQcConnection();
    	
    	String domainBack = connection.getPropertyAsString("DomainName");
    	String proyectBack = connection.getPropertyAsString("ProjectName");
		 
    	Dispatch.call(connection, "Disconnect");
    	
    	Dispatch.call(connection, "Connect", domain, proyect);
		 
    	ActiveXComponent userGroupsList = connection.getPropertyAsComponent("UserGroupsList");
		 
    	int cnt = userGroupsList.getPropertyAsInt("count");
		 
    	List<String> tmp = new ArrayList<String>();
		 
    	for(int x=1; x<=cnt;x++) {
    		String member = userGroupsList.invoke("Item", new Variant(x)).getString();
    		if(!MEMBER_RESTRICTED.contains(member.toUpperCase())) {
    			tmp.add(member);
    		}
    	}

    	Dispatch.call(connection, "Disconnect");
    	
    	Dispatch.call(connection, "Connect", domainBack, proyectBack);
		 
    	return tmp.toArray(new String[]{});
    	
    }
    
    /*public Map<String,String[]> getDomainsWithProyect() {

    	ActiveXComponent connection = getQcConnection().getConnection();
    	
    	Map<String,String[]> domainsProyect = new HashMap<String, String[]>();
    	
    	ActiveXComponent visibleDomains = connection.getPropertyAsComponent("VisibleDomains");
		 
    	int domainsCnt = visibleDomains.getPropertyAsInt("count");
		 
    	for(int x=1; x<=domainsCnt;x++) {
    		String domain = visibleDomains.invoke("Item", new Variant(x)).getString();

    		ActiveXComponent visibleProjects = connection.invokeGetComponent("VisibleProjects",new Variant(domain));
    		int proyectsCnt = visibleProjects.getPropertyAsInt("count");
			
    		String[] proyectsList = new String[proyectsCnt];
			 
    		for(int y=1; y<=proyectsCnt;y++) {
    			String proyect = visibleProjects.invoke("Item", new Variant(y)).getString();
    			proyectsList[y-1] = proyect;
    		}
			 
    		domainsProyect.put(domain, proyectsList);
    	}
		
    	return domainsProyect;
    	
    }*/
    
}
