package org.chenile.base.exception;

public class NotFoundException extends ErrorNumException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8109926578240270390L;

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public NotFoundException (String message, Throwable cause) {
		super(404, message, cause);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public NotFoundException (int subErrorCode, String message) {
		super(404, subErrorCode, message);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public NotFoundException (int subErrorCode, Object[] params) {
		super(404, subErrorCode, params);
	}


	/**
	 * 
	 * @param message
	 */
	public NotFoundException ( String message) {
		super(404, message);
	}
	
	/**
	 * 
	 * @param message
	 * @param cause
	 * @param bhiveErrorCode
	 */
	public NotFoundException (String message, int subErrorNum,Throwable cause) {
		super(404,subErrorNum, message, cause);
	}
	
	
	/**
	 *
	 * @param subErrorNum
	 * @param message
	 * @param cause
	 */
	public NotFoundException(int subErrorNum, String message, Throwable cause) {
		super(404, subErrorNum, message, cause);
	}
	
	/**
	 *
	 * @param subErrorNum
	 * @param message
	 * @param cause
	 */
	public NotFoundException(int subErrorNum, Object[] params, Throwable cause) {
		super(404, subErrorNum, params, cause);
	}

}
