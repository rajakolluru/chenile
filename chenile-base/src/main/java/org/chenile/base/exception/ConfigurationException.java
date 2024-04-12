package org.chenile.base.exception;

public class ConfigurationException extends ErrorNumException {

	private static final long serialVersionUID = -8109926578240270390L;

	/**
	 * 
	 * @param subErrorNum
	 * @param message
	 */
	public ConfigurationException(int subErrorNum, String message) {
		super(500, subErrorNum, message);
	}

	/**
	 *
	 * @param subErrorNum
	 * @param params
	 */
	public ConfigurationException(int subErrorNum, Object[]params) {
		super(500, subErrorNum, params);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(500, message, cause);
	}

	/**
	 * 
	 * @param message
	 */
	public ConfigurationException(String message) {
		super(500, message);
	}
	
	/**
	 *
	 * @param subErrorNum
	 * @param message
	 * @param cause
	 */
	public ConfigurationException(int subErrorNum, String message, Throwable cause) {
		super(500, subErrorNum, message, cause);
	}

	/**
	 *
	 * @param subErrorNum
	 * @param params
	 * @param cause
	 */
	public ConfigurationException(int subErrorNum, Object[] params, Throwable cause) {
		super(500, subErrorNum, params, cause);
	}

}