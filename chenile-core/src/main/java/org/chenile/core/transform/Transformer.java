package org.chenile.core.transform;

import org.chenile.base.exception.BadRequestException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.owiz.Command;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Performs a transformation from JSON to the {@link ChenileExchange#bodyType} mentioned in the 
 * {@link ChenileExchange}
 * 
 * @author Raja Shankar Kolluru
 *
 */
public class Transformer extends BaseChenileInterceptor{

	private ObjectMapper om ;
	public Transformer(){
		om = new ObjectMapper();
		om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	@Override
	public void doPreProcessing(ChenileExchange exchange)  {		
		TypeReference<?> targetType = exchange.getBodyType();
		Object body = exchange.getBody();
		if (targetType == null || body == null) return;
		
		//if (body.getClass().isAssignableFrom(targetType.getType())) return;
		if (!(body instanceof String)) return;
			// throwBadRequestException(exchange);
		 exchange.setApiInvocation(null); // unset it so that it can be recomputed
		 System.out.println("*******Reset API INvocation");
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
