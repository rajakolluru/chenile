package org.chenile.base.response;

import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ErrorNumException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * A generic response that would be returned for all the requests made 
 * 
 * @author Raja Shankar Kolluru
 *
 * @param <T>
 */
public class GenericResponse<T> implements WarningAware{

	private boolean success = false;
	private List<ResponseMessage> errors;
	private ResponseMessage responseMessage = new ResponseMessage();
	
	@JsonProperty("payload") private T data;
	public GenericResponse() {}
	public GenericResponse(ErrorNumException e, ErrorType errorType) {
		this.responseMessage = e.getResponseMessage();
		if (e.getErrors() != null && e.getErrors().size() > 0) {
			this.errors = e.getErrors();
		}else
			this.addWarningMessage(e.getResponseMessage());
	}

	public GenericResponse(T data) {
		this.data = data;
		success = true;
		List<ResponseMessage> x = WarningAware.obtainWarnings(data);
		if (x != null && x.size() > 0) {
			this.errors = x;
			this.responseMessage = x.get(0).clone();		
		}
		// By default set it to OK. It will be over-ridden later if required
		// from the warning error code set in operation definition
		setCode(200); 
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	@JsonSerialize(using = ToStringSerializer.class)
	public int getCode() {
		return responseMessage.getCode();
	}

	@JsonSerialize(using = ToStringSerializer.class)
	public void setCode(int errorCode) {
		this.responseMessage.setCode(errorCode);
	}

	public ErrorType getSeverity() {
		return responseMessage.getSeverity();
	}

	public void setSeverity(ErrorType severity) {
		this.responseMessage.setSeverity(severity);
	}

	public String getDescription() {
		return responseMessage.getDescription();
	}

	public void setDescription(String message) {
		this.responseMessage.setDescription(message);
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	@JsonSerialize(using = ToStringSerializer.class)
	public int getSubErrorCode() {
		return this.responseMessage.getSubErrorCode();
	}

	@JsonSerialize(using = ToStringSerializer.class)
	public void setSubErrorCode(int subErrorCode) {
		this.responseMessage.setSubErrorCode(subErrorCode);
	}
	
	public List<ResponseMessage> getErrors() {
		return errors;
	}
	
	public void addWarningMessage(ResponseMessage m) {
		if (errors == null)
			errors = new ArrayList<>();
		errors.add(m);
	}
	@Override
	public List<ResponseMessage> getWarningMessages() {
		return errors;
	}
	@Override
	public void removeAllWarnings() {
		errors = null;		
	}

}
