package org.chenile.core.entrypoint;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.core.errorcodes.ErrorCodes;

public abstract class ExceptionHandler {
	public static void handleException(Exception e) {
		if ( e instanceof ErrorNumException) {
			throw (ErrorNumException)e;
		}
    	Throwable cause = e.getCause();
    	if (cause != null) {
    		handleRuntime(cause); // peel off the first wrapper exception
    	}
		handleRuntime(e);	
	}
	
	private static void handleRuntime(Throwable e) {	
		
		if (e instanceof RuntimeException) {
			throw (RuntimeException) e;
		}
		throw new ServerException(ErrorCodes.SERVICE_EXCEPTION.getSubError(),
				ErrorCodes.SERVICE_EXCEPTION.name() + ":" + e.getMessage(),e);
	}

}
