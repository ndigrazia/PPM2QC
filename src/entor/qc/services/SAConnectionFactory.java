package entor.qc.services;

import entor.qc.exception.QcException;

public class SAConnectionFactory {

	public static SAConnection createConnection(String pServerURL) throws QcException {
		return new SAConnection(pServerURL);
	}

}
