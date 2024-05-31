package org.chenile.core.errorcodes;

/**
 * ALl the errors that have been defined for Chenile core.
 * The error codes defined here must be defined in the Message resources so that the
 * error messages are i18n.
 */
public enum ErrorCodes {
	SERVICE_NOT_FOUND(501),
	HEALTH_CHECKER_NOT_CONFIGURED(502),
	NOT_INSTANCE_HEALTH_CHECKER(503),
	BODY_TYPE_SELECTOR_ERROR(504),
	MISSING_TRAJECTORY_ID(505),
	MISSING_SERVICE_REFERENCE(506),
	ILLEGAL_HEADER(507),
	UNKNOWN_METHOD(508),
	CANNOT_INVOKE_TARGET(509),
	CANNOT_CONVERT_HEADER(510),  NOT_CONFIGURED_IN_SPRING(511),
	CHENILE_EXCHANGE_ONLY(512), MISSING_INPUT_TYPE(513),
	MISSING_OPERATION(514),
	CANNOT_CONFIGURE_CHENILE_RESOURCE(515),
	TYPE_MISMATCH(516),
	UNKNOWN_EVENT(517),
	UNKNOWN_EVENT_SUBSCRIBED(518),
	EVENT_TYPE_MISMATCH(519),
	EVENT_RETURN_TYPE_MISMATCH(520),
	PDF_MISCONFIGURATION(521),
	SERVICE_EXCEPTION(522),
	INVALID_CONTROLLER_ARGS(523);
	final int subError;
	private ErrorCodes(int subError) {
		this.subError = subError;
	}

	public int getSubError() {
		return this.subError;
	}
}
