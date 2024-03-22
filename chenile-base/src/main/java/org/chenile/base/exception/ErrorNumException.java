package org.chenile.base.exception;

import java.util.ArrayList;
import java.util.List;

import org.chenile.base.response.ErrorType;
import org.chenile.base.response.ResponseMessage;

public class ErrorNumException extends RuntimeException {

	private static final long serialVersionUID = -6173788572115857426L;
	private ResponseMessage responseMessage = new ResponseMessage();
	private List<ResponseMessage> errors = new ArrayList<>();
	public void setResponseMessage(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}
	
	public ErrorNumException() {
		this.responseMessage.setCode(500);
		this.responseMessage.setSeverity(ErrorType.ERROR); 
	}
	
	public ErrorNumException(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
	}

	/**
	 * @param errorNum
	 * @param message
	 * @param cause
	 */
	public ErrorNumException(int errorNum, String message, Throwable cause) {
		super(message, cause);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}
	
	/**
	 * @param errorNum
	 * @param message
	 * @param cause
	 */
	public ErrorNumException(int errorNum, Object[] params, Throwable cause) {
		super(cause);
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}

	/**
	 * @param errorNum
	 * @param subErrorNum
	 * @param message
	 * @param cause
	 */
	public ErrorNumException(int errorNum, int subErrorNum, String message, Throwable cause) {
		super(message, cause);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}
	
	public ErrorNumException(int errorNum, int subErrorNum, Object[] params, Throwable cause) {
		super(cause);
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}

	public ErrorNumException(int errorNum, String message) {
		super(message);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}
	
	public ErrorNumException(int errorNum, Object[] params) {
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}

	/**
	 * 
	 * @param errorNum
	 * @param subErrorNum
	 * @param message
	 */
	public ErrorNumException(int errorNum, int subErrorNum, String message) {
		super(message);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}
	
	/**
	 * 
	 * @param errorNum
	 * @param subErrorNum
	 * @param message
	 */
	public ErrorNumException(int errorNum, int subErrorNum, Object[] params) {
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
	}

	/**
	 * 
	 * @return
	 */
	public int getErrorNum() {
		return this.responseMessage.getCode();
	}

	/**
	 * 
	 * @return
	 */
	public int getSubErrorNum() {
		return this.responseMessage.getSubErrorCode();
	}
	
	@Override
	public String getMessage() {
		return this.responseMessage.getDescription();
	}
	
	public void setMessage(String message) {
		this.responseMessage.setDescription(message);
	}

	public Object[] getParams() {
		return this.responseMessage.getParams();
	}

	public void setParams(Object[] params) {
		this.responseMessage.setParams(params);
	}

	@Override
	public String toString() {
		return "ErrorNumException [responseMessage=" + responseMessage + "]";
	}

	public ResponseMessage getResponseMessage() {
		return responseMessage;
	}

	public List<ResponseMessage> getErrors() {
		return errors;
	}

	public void setErrors(List<ResponseMessage> errors) {
		this.errors = errors;
	}

}
