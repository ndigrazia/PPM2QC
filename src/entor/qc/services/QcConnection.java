package entor.qc.services;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.ComFailException;
import com.jacob.com.Variant;

import entor.qc.exception.QcException;
import entor.qc.symbols.QcSymbols;

public class QcConnection {

    private ActiveXComponent connection;
    
    private String serverURL;
    
    public QcConnection(String pServerURL) throws QcException {
    	serverURL = pServerURL;
    }

    public void connect(String pUsername, String pPassword, String pDomain, String pProject) throws QcException {
		 
    	if (!isLoggedIn()) {
			 login(pUsername, pPassword);
    	} else {
			 if (pUsername.equals(getCurrentUser())) {
				 throw new QcException((new StringBuilder("Already logged as ")).append(getCurrentUser()).toString());
			 }
	 	}
		 
		connectToProject(pDomain, pProject);
		
    }
    
    public String getCurrentUser() throws QcException {
    	
    	if (!isLoggedIn()) {
    		return null;
    	} else {
    		return connection.invoke("userName").getString();
    	}
    	
    }
    
    public boolean isLoggedIn() throws QcException {
    	
    	if (connection != null && connection.invoke("loggedIn").getBoolean()) {
    		return true;
    	}
    	
    	return false;
    	
    }
	 
    private void initConnection() throws QcException {
	
    	if (connection != null)
			 return;

		 try {
			 connection = new ActiveXComponent(QcSymbols.OTA_API);
			 connection.invoke("InitConnectionEx", serverURL);
		 }
		 catch (ComFailException e) {
			 throw new QcException(e);
		 }
		 	
	 }

    public boolean isConnected() throws QcException {
    
    	if (connection != null && connection.invoke("connected").getBoolean()) {
    		return true;
    	}
    	
    	return false;
    
    }

    private void connectToProject(String pDomain, String pProject) throws QcException {
    	
    	if (!isLoggedIn()) {
    		throw new QcException("Need to log in first!");
    	}
    	
    	connection.invoke("connect", new Variant(pDomain), new Variant(pProject));
    	
    }

	public void login(String pUsername, String pPassword) throws QcException {
		 
		if (!isConnected()) {
			initConnection();
		}
		else {
			if (isLoggedIn()) {
				 throw new QcException("Need to disconnect first!");
			}
		}
		 
		try {
			connection.invoke("login", new Variant(pUsername), new Variant(pPassword));
		}
		catch (ComException e) {
			 throw new QcException("Invalid login and/or password", e);
		}
		 
	 }

	public void disconnect() throws QcException {
		
		try {
		 
			if (isLoggedIn()) {
		 		
		 		if (isConnected()) {
		 			connection.invoke("disconnectProject");
		 		}
		 		
		 		connection.invoke("logout");
		 		
		 	}
		 	
		 	connection.invoke("releaseConnection");
		 	
		 	connection = null;

		}
		catch (ComException e) {
			throw new QcException(e);
		}
		
	}

	public ActiveXComponent getConnection() {
		return connection;
	}

}
