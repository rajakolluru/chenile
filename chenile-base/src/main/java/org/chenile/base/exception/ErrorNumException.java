package org.chenile.base.exception;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import org.chenile.base.response.ErrorType;
import org.chenile.base.response.ResponseMessage;

public class ErrorNumException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = -6173788572115857426L;
	private ResponseMessage responseMessage = new ResponseMessage();
	private final List<ResponseMessage> errors = new ArrayList<>();
	
	public ErrorNumException() {
		this.responseMessage.setCode(500);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}
	
	public ErrorNumException(ResponseMessage responseMessage) {
		this.responseMessage = responseMessage;
		this.errors.add(responseMessage);
	}

	/**
	 *
	 * @param errorNum - The root error code - same as Http Response code
	 * @param message - a message in english. Ideally you should use Message bundles
	 * @param cause - the root cause of the error
	 */
	public ErrorNumException(int errorNum, String message, Throwable cause) {
		super(message, cause);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}

	/**
	 *
	 * @param errorNum - The root error code - same as Http Response code
	 * @param params - The Params that need to be passed to the message bundle
	 * @param cause - the root cause of the error
	 */
	public ErrorNumException(int errorNum, Object[] params, Throwable cause) {
		super(cause);
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}

	/**
	 * @param errorNum - the root error code - same as Http Response code
	 * @param subErrorNum - the sub error code
	 * @param message - the message in english. Ideally message bundles must be used.
	 * @param cause - the root cause of the error
	 */
	public ErrorNumException(int errorNum, int subErrorNum, String message, Throwable cause) {
		super(message, cause);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}

	/**
	 * @param errorNum - the root error code - same as Http Response code
	 * @param subErrorNum - the sub error code
	 * @param params - the params to be passed to the message bundle
	 * @param cause - the root cause of the error
	 */
	public ErrorNumException(int errorNum, int subErrorNum, Object[] params, Throwable cause) {
		super(cause);
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}

	/**
	 *  @param errorNum - the root error code - same as Http Response code
	 * 	@param message - the message in english. Ideally message bundles must be used.
	 */
	public ErrorNumException(int errorNum, String message) {
		super(message);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}
	/**
	 *  @param errorNum - the root error code - same as Http Response code
	 * 	@param params - the params to be passed to the message bundle
	 */
	public ErrorNumException(int errorNum, Object[] params) {
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}

	/**
	 * @param errorNum - the root error code - same as Http Response code
	 * @param subErrorNum - the sub error code
	 * @param message - the message in english. Ideally message bundles must be used.
	 */
	public ErrorNumException(int errorNum, int subErrorNum, String message) {
		super(message);
		this.responseMessage.setDescription(message);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}

	/**
	 /**
	 * @param errorNum - the root error code - same as Http Response code
	 * @param subErrorNum - the sub error code
	 * @param params - the params to be passed to the message bundle
	 */
	public ErrorNumException(int errorNum, int subErrorNum, Object[] params) {
		this.responseMessage.setParams(params);
		this.responseMessage.setCode(errorNum);
		this.responseMessage.setSubErrorCode(subErrorNum);
		this.responseMessage.setSeverity(ErrorType.ERROR);
		this.errors.add(responseMessage);
	}

	public int getErrorNum() {
		return this.responseMessage.getCode();
	}

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

	public void addError(ResponseMessage message){
		this.errors.add(message);
	}

}
