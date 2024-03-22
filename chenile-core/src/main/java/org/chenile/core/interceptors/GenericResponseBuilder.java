package org.chenile.core.interceptors;

import java.util.List;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.ErrorType;
import org.chenile.base.response.GenericResponse;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.OperationDefinition;
import org.springframework.http.HttpStatus;

/**
 * Constructs a generic response from the response and exception that has been thrown. 
 * This also sets the Http Status and warnings in the exchange so that the HTTP controller can 
 * access them directly from exchange.
 * This should be the first interceptor in the chain.
 * @author Raja Shankar Kolluru
 *
 */
public class GenericResponseBuilder extends BaseChenileInterceptor{

	@Override
	protected void doPostProcessing(ChenileExchange chenileExchange) {
		if (chenileExchange.getException() != null) {
			processFailure(chenileExchange);
		}else {
			processSuccess(chenileExchange);
		}
	}
	
	private HttpStatus getSuccessHttpStatus(ChenileExchange exchange) {
		Object response = exchange.getResponse();
		OperationDefinition operationDefinition = exchange.getOperationDefinition();
		List<ResponseMessage> x = WarningAware.obtainWarnings(response);
		if (x == null || x.size() == 0)
			return HttpStatus.valueOf(operationDefinition.getSuccessHttpStatus());
		else 
			return HttpStatus.valueOf(operationDefinition.getWarningHttpStatus());
	}
	
	private void processFailure(ChenileExchange exchange)  {
		Throwable exception = exchange.getException();
		ErrorNumException errorNumException;
		GenericResponse<Object> genericResponse;

		if (exception instanceof ErrorNumException) {
			errorNumException = (ErrorNumException) exception;
		} else {
			errorNumException = new ErrorNumException(500, exception.getMessage());			
		}
		genericResponse = new GenericResponse<Object>(errorNumException, ErrorType.ERROR);
		exchange.setResponse(genericResponse);
		exchange.setHttpResponseStatusCode(errorNumException.getErrorNum());
		exchange.setResponseMessages(errorNumException.getErrors());
	}
	
	private void processSuccess(ChenileExchange exchange)  {
		Object response = exchange.getResponse();
		int httpResponseCode = getSuccessHttpStatus(exchange).value();
		GenericResponse<Object> genericResponse = new GenericResponse<Object>(response);
		genericResponse.setCode(httpResponseCode);
		exchange.setResponse(genericResponse);
		exchange.setHttpResponseStatusCode(httpResponseCode);
		List<ResponseMessage> x =  WarningAware.obtainWarnings(response);
		if (x == null) return;
		WarningAware.removeAllWarnings(response); // warnings can be removed from the response since
		// GenericResponse already has warnings inside.
		exchange.setResponseMessages(x);
	}
	
}
