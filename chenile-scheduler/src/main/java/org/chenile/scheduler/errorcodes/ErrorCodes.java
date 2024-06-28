package org.chenile.scheduler.errorcodes;

public enum ErrorCodes {
	MISCONFIGURATION_DUPLICATE_JOB_NAME(570);
	final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}
	
	public int getSubError() {
		return this.subError;
	}
}
