package org.chenile.core.transform;

import org.chenile.base.exception.BadRequestException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.owiz.Command;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Performs a transformation from JSON to the {@link ChenileExchange#bodyType} mentioned in the 
 * {@link ChenileExchange}
 * 
 * @author meratransport
 *
 */
public class Transformer implements Command<ChenileExchange>{

	@Override
	public void execute(ChenileExchange exchange) throws Exception {		
		Class<?> targetType = exchange.getBodyType();
		Object body = exchange.getBody();
		if (targetType == null || body == null) return;
		if (body.getClass().isAssignableFrom(targetType)) return;
		if (!(body instanceof String)) 
			throwBadRequestException(exchange);
		 
		ObjectMapper om = new ObjectMapper();
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		try {
			Object newBody = om.readValue((String)body, targetType);
			exchange.setBody(newBody);
		} catch (Exception e) {
			throwBadRequestException(exchange);
		}
		
	}

	private void throwBadRequestException(ChenileExchange exchange) {
		throw new BadRequestException(ErrorCodes.TYPE_MISMATCH.ordinal(), 
				"Type of payload passed does not match the operation for " + exchange.getServiceDefinition().getName()
				+ "." + exchange.getOperationDefinition().getName());		
	}

}
