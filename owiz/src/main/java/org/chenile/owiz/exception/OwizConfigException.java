package org.chenile.owiz.exception;

public class OwizConfigException  extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4741496974371948228L;
	
	/**
	 * 
	 * @param message
	 */
	public OwizConfigException( String message) {
		super(message);
	}

	/**
	 * 
	 * @param message
	 * @param cause
	 */
	public OwizConfigException(String message, Throwable cause) {
		super(message,cause);
	}

	

	


}
