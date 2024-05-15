package org.chenile.proxy.errorcodes;

public enum ErrorCodes {
	
	CANNOT_CONNECT(650), CANNOT_INVOKE(651), MISSING_BODY(652),
	;
	final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}
	
	public int getSubError() {
		return this.subError;
	}
}
