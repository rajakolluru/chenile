package org.chenile.base.exception;

public class BadRequestException extends ErrorNumException {

	private static final long serialVersionUID = -8109926578240270390L;

	/**
	 * 
	 * @param subErrorNum
	 * @param message
	 */
	public BadRequestException(int subErrorNum, String message) {
		super(400, subErrorNum, message);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public BadRequestException(String message, Throwable cause) {
		super(400, message, cause);
	}

	/**
	 * 
	 * @param message
	 */
	public BadRequestException(String message) {
		super(400, message);
	}
	
	/**
	 *
	 * @param subErrorNum
	 * @param message
	 * @param cause
	 */
	public BadRequestException(int subErrorNum, String message, Throwable cause) {
		super(400, subErrorNum, message, cause);
	}

}