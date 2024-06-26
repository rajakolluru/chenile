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

/**
 * This communicates with SpringRestTemplate to find the errors from the returned response.
 */
public class ChenileResponseHandler extends DefaultResponseErrorHandler{

	private final ChenileExchange chenileExchange;
	private final ObjectMapper objectMapper;
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
		HttpStatus statusCode = HttpStatus.resolve(response.getStatusCode().value());
		if (statusCode != null) {
			byte[] body = getResponseBody(response);
			if (body.length == 0) {
				RuntimeException e1 = new ServerException("Error happened in invoking " + getEndpointName() +
		            response.getStatusCode().value() + response.getStatusText() + " "  +
						response.getBody());
				chenileExchange.setException(e1);
				return;
			}
			parseBody(body,response);
			return;
		}		
	}
	
	protected void parseBody(byte[] body,ClientHttpResponse response) throws IOException{
		try {

			GenericResponse<?> gr = (GenericResponse<?>) objectMapper.readValue(body, getTypeReference());
			ErrorNumException e1 = new ErrorNumException(gr.getCode(),gr.getSubErrorCode(),
					gr.getDescription());
			chenileExchange.setException(e1);
			return;
		}catch(Exception e) {
			ErrorNumException e1 = new ServerException("Error happened in invoking " + getEndpointName() +
		            response.getStatusCode().value() + response.getStatusText() );
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

