package org.chenile.mqtt.errorcodes;

/**
 * Chenile MQTT error codes.
 */
public enum ErrorCodes {

	MISCONFIGURATION(900), UNSUPPORTED_TOPIC_FORMAT_FOR_OPERATION(901), UNSUPPORTED_TOPIC_FORMAT_FOR_SERVICE(902),
	MISSING_SERVICE(903), MISSING_SERVICE_OPERATION(904), CANNOT_FIND_TOPIC(905);

	final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}
	
	public int getSubError() {
		return this.subError;
	}
}
