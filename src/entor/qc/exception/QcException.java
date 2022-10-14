package entor.qc.exception;

import com.jacob.com.ComException;
import com.jacob.com.ComFailException;

public class QcException extends Exception {
	
	 public QcException(ComException pCause) {
		 super(getComMessage(pCause), pCause);
	 }

	 public QcException(String pMessage) {
		 super(pMessage);
	 }

	 public QcException(String pMessage, Throwable pCause) {
		 super(pMessage, pCause);
	 }

	 public String getMessage() {
		 if (getCause() instanceof ComFailException) {
			 return getComMessage((ComFailException)getCause());
		 } else {
			 return super.getMessage();
		 }
	 }

	 private static String getComMessage(ComException pComFailException) {
		 String lMsg = pComFailException.getMessage();
		 
		 if (lMsg != null) {
			int idx = lMsg.indexOf("Description:");
		 	if (idx != -1) {
		 		return lMsg.substring(idx + 12).trim();
		 	} else {
		 		return lMsg;
		   	}
		 }
		 
		 if (pComFailException.getHelpFile() != null) {
			 return pComFailException.getHelpFile();
		 } else {
			 return "";
		 }
	 }
		 
}
