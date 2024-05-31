package org.chenile.base.response;

import java.util.Arrays;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * A detailed response message that contains a bunch of information.
 * Code - HTTP Status code
 * subErrorCode - The specific error returned by the service. Typically, there must be
 * an enum that the service defines which has all the possible values for the subErrorCode
 * that the particular service returns. Sub error codes are mapped to an i18n message that can
 * have substitutable parameters.
 * Description - of the error. Must be internationalized and must be found in a message bundle
 * Field - at which the error occurred. This is useful if there is a validation failure for a particular field
 * Severity - which can have the value WARN or ERROR
 * Params - which contain replacements for the substitutable parameters contained in the i18n message
 */
public class ResponseMessage {
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String message) {
		this.description = message;
	}
	
	@JsonSerialize(using = ToStringSerializer.class)
	public int getCode() {
		return code.value();
	}
	@JsonSerialize(using = ToStringSerializer.class)
	public void setCode(int code) {
		this.code = HttpStatus.valueOf(code);
	}
	
	public void setCode(HttpStatus code) {
		this.code = code;
	}
	
	public Object[] getParams() {
		return params;
	}
	public void setParams(Object[] params) {
		this.params = params;
	}
	
	private String field;
	/**
	 * A message. Can be a translated message after applying i18n
	 */
	private String description; 
	/**
	 * The HTTP Status Code - default to internal server error
	 */
	private HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;  
	/**
	 * The error code specific to the service
	 */
	private int subErrorCode; 
	/**
	 * Params that might be needed to render the description. 
	 * These contain substitutable parameters
	 * Ex: Resource {0} not found
	 *
	 */
	private Object[] params;
	private ErrorType severity;
	
	public ErrorType getSeverity() {
		return severity;
	}
	public void setSeverity(ErrorType severity) {
		this.severity = severity;
	}
	
	@Override
	public String toString() {
		return "ResponseMessage [description=" + description + ", HTTP Status Code=" + code + ", subErrorCode=" + subErrorCode +
				"params=" + Arrays.toString(params) + ", severity=" + severity + ", field=" + field + "]";
	}
	
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	@JsonSerialize(using = ToStringSerializer.class)
	public int getSubErrorCode() {
		return subErrorCode;
	}
	@JsonSerialize(using = ToStringSerializer.class)
	public void setSubErrorCode(int subErrorCode) {
		this.subErrorCode = subErrorCode;
	}
	
	public ResponseMessage clone() {
		ResponseMessage m = new ResponseMessage();
		m.setCode(code);
		m.setDescription(description);
		m.setField(field);
		m.setParams(params);
		m.setSeverity(severity);
		m.setSubErrorCode(subErrorCode);
		return m;
	}
}
