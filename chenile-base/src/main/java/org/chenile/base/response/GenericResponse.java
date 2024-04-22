package org.chenile.base.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic response that would be returned for all the services. It includes the payload
 * returned by the service along with other information such as Http Status code, warnings and errors.
 * This allows a comprehensive and consistent way of communicating with the rest of the world.<br/>
 * This can be used across protocols though it uses the Http status code
 * 
 * @author Raja Shankar Kolluru
 *
 * @param <T> - the Generic for the actual payload returned by the service
 */
public class GenericResponse<T> implements WarningAware{

	private boolean success = false;
	private List<ResponseMessage> errors;
	private ResponseMessage responseMessage = new ResponseMessage();
	
	@JsonProperty("payload") private T data;
	public GenericResponse() {}
	public GenericResponse(ResponseMessage message) {
		this.responseMessage = message;
	}

	public GenericResponse(T data) {
		this.data = data;
		success = true;
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
	@Override @JsonIgnore
	public List<ResponseMessage> getWarningMessages() {
		return errors;
	}
	@Override
	public void removeAllWarnings() {
		errors = null;		
	}
}
