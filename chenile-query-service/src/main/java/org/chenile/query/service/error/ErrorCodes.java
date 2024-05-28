package org.chenile.query.service.error;

public enum ErrorCodes {
	COUNT_QUERY_DOES_NOT_RETURN_INT(720),
	CANNOT_EXECUTE_QUERY(721),
	CANNOT_EXECUTE_COUNT_QUERY(722);
	private final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}

	public int getSubError() {
		return this.subError;
	}
}
