package org.chenile.proxy.interceptors;


import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map.Entry;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.base.response.GenericResponse;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.Command;
import org.chenile.proxy.builder.ProxyBuilder;
import org.chenile.proxy.errorcodes.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Invokes the remote service using HTTP
 */
public class HttpInvoker implements Command<ChenileExchange>{
	
	@Autowired RestTemplateBuilder restTemplateBuilder;
	private ObjectMapper objectMapper = new ObjectMapper();
	public HttpInvoker() {}	
	@Override
	@SuppressWarnings("unchecked")
	public void execute(ChenileExchange exchange) throws Exception {
		OperationDefinition od = exchange.getOperationDefinition();
		HttpHeaders headers = extractHeaders(exchange);
	    HttpEntity<Object> entity = new HttpEntity<Object>(exchange.getBody(),headers);
	      
	    String baseURI = (String)exchange.getHeader(ProxyBuilder.REMOTE_URL_BASE);
		String serviceOpName = exchange.getServiceDefinition().getId() + "." +
				exchange.getOperationDefinition().getName();
		if (!baseURI.startsWith("http://")) baseURI = "http://" + baseURI;
	    ResponseEntity<GenericResponse<?>> httpResponse = null;
	    RestTemplate restTemplate = getRestTemplate(exchange);
		try {
			httpResponse = (ResponseEntity<GenericResponse<?>>)
					restTemplate.exchange(baseURI + constructUrl(od.getUrl(),exchange),
							httpMethod(od), entity,exchange.getResponseBodyType());
		} catch (RestClientException e) {
			Object[] eArgs = new Object[]{baseURI, serviceOpName, e.getMessage()};
			setCorrectException(exchange,eArgs,e);
			return;
		}
		populateResponse(httpResponse,baseURI,serviceOpName,exchange);
	}

	private void populateResponse(ResponseEntity<GenericResponse<?>> httpResponse, String baseURI,String serviceOpName, ChenileExchange exchange) {
		if (httpResponse.hasBody()) {
			GenericResponse<?> gr = httpResponse.getBody();
			exchange.setResponse(gr.getData());
		}else {
			String message = "No body returned for " + httpResponse.toString();
			exchange.setException(new ServerException(ErrorCodes.MISSING_BODY.getSubError(), new Object[] {baseURI, serviceOpName}));
		}
	}

	private void setCorrectException(ChenileExchange exchange, Object[] eArgs, RestClientException e){
		if (exchange.getException() != null)
			return; // if this has already been handled by the error handler then
		// the exception has already been set. So we can return
		Throwable t = e.getCause();
		ErrorNumException exc;
		if (t instanceof ConnectException || t instanceof UnknownHostException ||
				t.getClass().getName().startsWith("java.net.")){
			exc = new ServerException(ErrorCodes.CANNOT_CONNECT.getSubError(), eArgs,e);
		}else {
			exc = new ServerException(ErrorCodes.CANNOT_INVOKE.getSubError(), eArgs,e);
		}
		exchange.setException(exc);
	}

	/**
	 * Constructs the URL taking care of path variables
	 * @param url - the url inclusive of path variable
	 * @param exchange - the chenile exchange
	 * @return the new URL with paths substituted if required
	 */
	private static String constructUrl(String url, ChenileExchange exchange){
		while (url.contains("/{")) {
			int startIndex = url.indexOf("/{");
			int endIndex = url.indexOf("}");
			String pathVarName = url.substring(startIndex+2,endIndex);
			Object pathValue  = exchange.getHeader(pathVarName);
			url = url.substring(0,startIndex+1) + pathValue + url.substring(endIndex+1);
		}
		return url;
	}
	
	private HttpHeaders extractHeaders(ChenileExchange exchange) {
		HttpHeaders headers = new HttpHeaders();
	    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	    for (Entry<String, Object> entry: exchange.getHeaders().entrySet()) {
			String key = entry.getKey();
			Object obj = entry.getValue();
			headers.add(key, obj.toString());
		}
	    return headers;
	}
	
	private HttpMethod httpMethod(OperationDefinition od) {
        return switch (od.getHttpMethod()) {
            case GET -> HttpMethod.GET;
            case POST -> HttpMethod.POST;
            case DELETE -> HttpMethod.DELETE;
            case PUT -> HttpMethod.PUT;
            case PATCH -> HttpMethod.PATCH;
        };
    }
	
	protected RestTemplate getRestTemplate(ChenileExchange chenileExchange) {
		ChenileResponseHandler responseErrorHandler = new ChenileResponseHandler(chenileExchange,objectMapper);
		return restTemplateBuilder.errorHandler(responseErrorHandler).build();
	}
}
