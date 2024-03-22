package org.chenile.proxy.interceptors;

import java.io.IOException;
import java.lang.reflect.Type;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.base.response.GenericResponse;
import org.chenile.core.context.ChenileExchange;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ChenileResponseHandler extends DefaultResponseErrorHandler{

	private ChenileExchange chenileExchange;
	private ObjectMapper objectMapper;
	public ChenileResponseHandler(ChenileExchange exchange,ObjectMapper objectMapper) {
		this.chenileExchange = exchange;
		this.objectMapper = objectMapper;
	}
	
	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
		return !(httpResponse.getStatusCode().is2xxSuccessful());
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {		
		HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
		if (statusCode != null) {
			byte[] body = getResponseBody(response);
			if (body == null || body.length == 0) {
				RuntimeException e1 = new ServerException("Error happened in invoking " + getEndpointName() +
		            response.getRawStatusCode() + response.getStatusText() );
				chenileExchange.setException(e1);
				return;
			}
			parseBody(body,response);
			return;
		}		
	}
	
	protected void parseBody(byte[] body,ClientHttpResponse response) throws IOException{
		try {
			System.out.println("Body is " + new String(body));
			GenericResponse<?> gr = objectMapper.readValue(body, getTypeReference());
			System.out.println("error code is " + gr.getCode());
			ErrorNumException e1 = new ErrorNumException(gr.getCode(),gr.getSubErrorCode(),
					gr.getDescription());
			chenileExchange.setException(e1);
			return;
		}catch(Exception e) {
			e.printStackTrace();
			ErrorNumException e1 = new ServerException("Error happened in invoking " + getEndpointName() +
		            response.getRawStatusCode() + response.getStatusText() );
			chenileExchange.setException(e1);
		}
	}
	
	protected TypeReference<?> getTypeReference(){
		return new TypeReference<Object>() {
			@Override
		    public  Type getType() {
		        return chenileExchange.getResponseBodyType().getType();
		    }
		};
	}
	
	protected String getEndpointName() {
		return chenileExchange.getServiceDefinition().getId() + "." + 
				chenileExchange.getOperationDefinition().getName();
	}	
}

