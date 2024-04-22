package org.chenile.core.interceptors;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.GenericResponse;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.model.OperationDefinition;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * Constructs a generic response from the response and exception that has been thrown. 
 * This also sets the Http Status and warnings in the exchange so that the HTTP controller can 
 * access them directly from exchange.
 * This should be the first interceptor in the chain.
 * @author Raja Shankar Kolluru
 *
 */
public class GenericResponseBuilder extends BaseChenileInterceptor {

	@Override
	protected void doPostProcessing(ChenileExchange chenileExchange) {
		if (chenileExchange.getException() != null) {
			processFailure(chenileExchange);
		} else {
			processSuccess(chenileExchange);
		}
	}

	private HttpStatus getSuccessHttpStatus(ChenileExchange exchange) {
		Object response = exchange.getResponse();
		OperationDefinition operationDefinition = exchange.getOperationDefinition();
		List<ResponseMessage> x = WarningAware.obtainWarnings(response);
		if (x == null || x.isEmpty())
			return HttpStatus.valueOf(operationDefinition.getSuccessHttpStatus());
		else
			return HttpStatus.valueOf(operationDefinition.getWarningHttpStatus());
	}

	private void processFailure(ChenileExchange exchange) {
		ErrorNumException errorNumException = exchange.getException();
		GenericResponse<Object> genericResponse = new
				GenericResponse<Object>(errorNumException.getResponseMessage());
		exchange.setResponse(genericResponse);
		exchange.setHttpResponseStatusCode(errorNumException.getErrorNum());
		populateResponseMessages(genericResponse, exchange);
	}

	private void populateResponseMessages(GenericResponse<Object> genericResponse, ChenileExchange exchange) {
		List<ResponseMessage> errorMessages = exchange.getResponseMessages();
		if (errorMessages == null || errorMessages.isEmpty()) return;
		int index = 0;
		for (ResponseMessage m : errorMessages) {
			if (index++ == 0) {
				genericResponse.setCode(m.getCode());
				exchange.setHttpResponseStatusCode(m.getCode());
				genericResponse.setSeverity(m.getSeverity());
				genericResponse.setSubErrorCode(m.getSubErrorCode());
				genericResponse.setDescription(m.getDescription());
			}
			genericResponse.addWarningMessage(m);
		}
	}

	private void processSuccess(ChenileExchange exchange) {
		Object response = exchange.getResponse();
		int httpResponseCode = getSuccessHttpStatus(exchange).value();
		GenericResponse<Object> genericResponse = new GenericResponse<Object>(response);
		genericResponse.setCode(httpResponseCode);
		exchange.setResponse(genericResponse);
		exchange.setHttpResponseStatusCode(httpResponseCode);
		populateResponseMessages(genericResponse, exchange);
		List<ResponseMessage> x = WarningAware.obtainWarnings(response);
		if (x != null)
			WarningAware.removeAllWarnings(response);
	}
}