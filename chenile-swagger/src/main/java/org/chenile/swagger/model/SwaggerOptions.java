/**
 * 
 */
package org.chenile.swagger.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ResponseMessage;

/**
 * @author Deepak N
 *
 */

public class SwaggerOptions implements Serializable {
	private static final long serialVersionUID = -7402037671681597902L;
	private List<Header> headers = Collections.emptyList();
	private List<ResponseMessage> responseMessageList = Collections.emptyList();;

	public List<ResponseMessage> getResponseMessageList() {
		return responseMessageList;
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public void setResponseCodes(List<ResponseCode> responseCodes) {
		populateResponseMessages(responseCodes);		
	}
	
	private void populateResponseMessages(List<ResponseCode> responseCodes) {
		this.responseMessageList = new ArrayList<>();
		for (ResponseCode rcode: responseCodes) {
			this.responseMessageList.add(createResponseMessage(rcode.getCode(), rcode.getMessage()));			
		}
	}
	
	private  ResponseMessage createResponseMessage(final int code, final String message) {
		return new ResponseMessageBuilder().code(code).message(message).build();
	}
	
}
