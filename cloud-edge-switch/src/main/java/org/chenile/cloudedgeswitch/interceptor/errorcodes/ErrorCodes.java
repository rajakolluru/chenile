package org.chenile.cloudedgeswitch.interceptor.errorcodes;

public enum ErrorCodes {
	
	PROCESSED_LOCALLY(8000), ERROR_DETAIL(8001), CANNOT_INVOKE_CLOUD(8002),
	LOCAL_SERVICE_FAILED(8003)
	;
	final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}
	
	public int getSubError() {
		return this.subError;
	}
}
