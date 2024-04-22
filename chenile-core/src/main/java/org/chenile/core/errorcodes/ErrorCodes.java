package org.chenile.core.errorcodes;

public enum ErrorCodes {
	
	CANNOT_INVOKE_TARGET(509), MISCONFIGURATION(2), SERVICE_EXCEPTION(3),
	TYPE_MISMATCH(4), UNKNOWN_EVENT(5), CANNOT_CONFIGURE_CHENILE_RESOURCE(6),
	CANNOT_CONVERT_HEADER(510);
	final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}
	
	public int getSubError() {
		return this.subError;
	}
}
