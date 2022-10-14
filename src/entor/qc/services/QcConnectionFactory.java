package entor.qc.services;

import entor.qc.exception.QcException;

public class QcConnectionFactory {

	public static QcConnection createConnection(String pServerURL) throws QcException {
		return new QcConnection(pServerURL);
	}

}
