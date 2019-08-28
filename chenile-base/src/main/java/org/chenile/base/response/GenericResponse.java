package org.chenile.base.response;

import org.chenile.base.exception.ErrorNumException;
import org.springframework.http.HttpStatus;

/**
 * A generic response that would be returned for all the requests made to this
 * controller
 * 
 * @author meratransport
 *
 * @param <T>
 */
public class GenericResponse<T> {

	private boolean success = false;

	private int errorCode;

	private ErrorType errorType;

	private String message;

	private T data;
	
	private int subErrorCode;

	public GenericResponse(ErrorNumException e, ErrorType errorType) {
		this.errorType = errorType;
		this.success = false;
		this.message = e.getMessage();
		this.errorCode = e.getErrorNum();
		this.subErrorCode = e.getSubErrorNum();
	}

	public GenericResponse(T data) {
		this.data = data;
		success = true;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public ErrorType getErrorType() {
		return errorType;
	}

	public void setErrorType(ErrorType errorType) {
		this.errorType = errorType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public int getSubErrorCode() {
		return subErrorCode;
	}

	public void setSubErrorCode(int subErrorCode) {
		this.subErrorCode = subErrorCode;
	}

}
