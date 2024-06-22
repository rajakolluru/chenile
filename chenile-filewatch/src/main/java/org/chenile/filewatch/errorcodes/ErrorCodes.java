package org.chenile.filewatch.errorcodes;

public enum ErrorCodes {
	CANNOT_MOVE_TO_PROCESSED(101), MISSING_HEADER_PROPERTIES(102), INVALID_ENCODING_TYPE(103),
	CANNOT_PROCESS_FILE(104), CANNOT_SERIALIZE_RESPONSE_TO_JSON(105), ERROR_IN_SERVICE(106), MISCONFIGURATION(107);
	final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}
	
	public int getSubError() {
		return this.subError;
	}
}
