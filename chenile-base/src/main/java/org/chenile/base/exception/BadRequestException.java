package org.chenile.base.exception;

import java.io.Serial;

public class BadRequestException extends ErrorNumException {

	@Serial private static final long serialVersionUID = -8109926578240270390L;

	/**
	 * 
	 * @param subErrorNum sub error code
	 * @param message the exception message
	 */
	public BadRequestException(int subErrorNum, String message) {
		super(400, subErrorNum, message);
	}

	/**
	 *
	 * @param subErrorNum sub error code
	 * @param params params that need to be substituted in the resource bundle
	 */
	public BadRequestException(int subErrorNum, Object[]params) {
		super(400, subErrorNum, params);
	}

	/**
	 * 
	 * @param message the exception message
	 * @param cause the original cause of the exception
	 */
	public BadRequestException(String message, Throwable cause) {
		super(400, message, cause);
	}

	/**
	 * 
	 * @param message the exception message
	 */
	public BadRequestException(String message) {
		super(400, message);
	}
	
	/**
	 *
	 * @param subErrorNum sub error code
	 * @param message the exception message
	 * @param cause the original cause of the exception
	 */
	public BadRequestException(int subErrorNum, String message, Throwable cause) {
		super(400, subErrorNum, message, cause);
	}

	/**
	 *
	 * @param subErrorNum sub error code
	 * @param params params that need to be substituted in the resource bundle
	 * @param cause the original cause of the exception
	 *
	 */
	public BadRequestException(int subErrorNum, Object[] params, Throwable cause) {
		super(400, subErrorNum, params, cause);
	}

}