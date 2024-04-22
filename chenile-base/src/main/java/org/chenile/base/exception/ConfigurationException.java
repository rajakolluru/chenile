package org.chenile.base.exception;

public class ConfigurationException extends ErrorNumException {

	private static final long serialVersionUID = -8109926578240270390L;

	public ConfigurationException(int subErrorNum, String message) {
		super(500, subErrorNum, message);
	}

	public ConfigurationException(int subErrorNum, Object[]params) {
		super(500, subErrorNum, params);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(500, message, cause);
	}
	public ConfigurationException(String message) {
		super(500, message);
	}

	public ConfigurationException(int subErrorNum, String message, Throwable cause) {
		super(500, subErrorNum, message, cause);
	}

	public ConfigurationException(int subErrorNum, Object[] params, Throwable cause) {
		super(500, subErrorNum, params, cause);
	}
}