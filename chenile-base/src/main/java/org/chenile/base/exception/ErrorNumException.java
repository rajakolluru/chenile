package org.chenile.base.exception;

public class ErrorNumException extends RuntimeException {

	@Override
	public String toString() {
		return "ErrorNumException [errorNum=" + errorNum + ", subErrorNum=" + subErrorNum + ", getMessage()="
				+ getMessage() + "]";
	}

	/**
	 *  
	 */
	private static final long serialVersionUID = -6173788572115857426L;
	private final int errorNum;
	private final int subErrorNum;
	
	public ErrorNumException() {
		this.errorNum = 500;
		this.subErrorNum = 0;
	}

	/**
	 * @param errorNum
	 * @param message
	 * @param cause
	 */
	public ErrorNumException(int errorNum, String message, Throwable cause) {
		super(message, cause);
		this.errorNum = errorNum;
		subErrorNum = 0;
	}

	/**
	 * @param errorNum
	 * @param subErrorNum
	 * @param message
	 * @param cause
	 */
	public ErrorNumException(int errorNum, int subErrorNum, String message, Throwable cause) {
		super(message, cause);
		this.errorNum = errorNum;
		this.subErrorNum = subErrorNum;
	}

	/**
	 * 
	 * @param errorNum
	 * @param message
	 */
	public ErrorNumException(int errorNum, String message) {
		super(message);
		this.errorNum = errorNum;
		subErrorNum = 0;
	}

	/**
	 * 
	 * @param errorNum
	 * @param subErrorNum
	 * @param message
	 */
	public ErrorNumException(int errorNum, int subErrorNum, String message) {
		super(message);
		this.errorNum = errorNum;
		this.subErrorNum = subErrorNum;
	}

	/**
	 * 
	 * @return
	 */
	public int getErrorNum() {
		return this.errorNum;
	}

	/**
	 * 
	 * @return
	 */
	public int getSubErrorNum() {
		return subErrorNum;
	}
	
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return super.getMessage();
	}

}
