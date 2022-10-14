package entor.qc.services;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComException;
import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;

import entor.qc.exception.QcException;
import entor.qc.symbols.QcSymbols;

public class SAConnection {

    private ActiveXComponent connection;
    
    private String serverURL;
    
    public SAConnection(String pServerURL) throws QcException {
    	serverURL = pServerURL;
    }

    public void connect(String pUsername, String pPassword) throws QcException {
		 
    	if (!isLoggedIn()) {
			 login(pUsername, pPassword);
    	} else {
    		throw new QcException("Already logged.");
    	}
		 
    }
    
    public boolean isLoggedIn() throws QcException {
    	
    	if (connection != null) {
    		return true;
    	}
    	
    	return false;
    	
    }
	 
    private void login(String pUsername, String pPassword) throws QcException {
	
    	if (connection != null)
			 return;

		 try {
			 connection =  new ActiveXComponent(QcSymbols.SA_API); 
			 Dispatch.call(connection, "Login", serverURL, pUsername, pPassword);
		 }
		 catch (ComFailException e) {
			 throw new QcException(e);
		 }
		 
    }
       
	public void disconnect() throws QcException {
		
		try {
			if (isLoggedIn()) {
				Dispatch.call(connection, "Logout");
		 	}
		 	
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
