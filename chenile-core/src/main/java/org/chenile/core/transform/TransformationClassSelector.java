package org.chenile.core.transform;

import java.lang.reflect.Type;

import org.chenile.base.exception.BadRequestException;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.Command;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Determines the target type for the body of the ChenileExchange and populates that into 
 * {@link ChenileExchange#getBodyType()}
 * Uses registered bodyTypeSeletors either at {@link ChenileServiceDefinition} or {@link OperationDefinition}
 * 
 * @author Raja Shankar Kolluru
 *
 */

public class TransformationClassSelector extends BaseChenileInterceptor{

	@Autowired SubclassRegistry subclassRegistry;
	@Override
	public void doPreProcessing(ChenileExchange exchange) {
		// Has a body type selector been registered against the service definition or operation definition
		// if so invoke it
		Command<ChenileExchange> bts = obtainBodyTypeSelector(exchange);
		if (bts != null) {
			try {
				bts.execute(exchange);
			}catch(Exception e) {
				throw new ServerException(ErrorCodes.BODY_TYPE_SELECTOR_ERROR.getSubError(), new Object[] {});
			}
		}else
			setDefaultBodyType(exchange);
		setSubclassIfRequired(exchange);
	}

	private void setSubclassIfRequired(ChenileExchange exchange) {
		TypeReference<?> ref = exchange.getBodyType();
		if (ref == null) return;
		if (!(exchange.getBody() instanceof String body)) return;
		if (ref.getType() instanceof Class clazz){
			try {
				Class<?> c = subclassRegistry.determineSubClass(body, clazz);
				if (c != null) exchange.setBodyType(convertClassToTypeReference(c));
			}catch(Exception e){
				throw new BadRequestException(ErrorCodes.PAYLOAD_CANNOT_BE_PARSED.getSubError(),
						new Object[] { body, e.getMessage()}, e);
			}
		}
	}


	private Command<ChenileExchange> obtainBodyTypeSelector(ChenileExchange exchange) {
		ChenileServiceDefinition sd = exchange.getServiceDefinition();
		OperationDefinition od = exchange.getOperationDefinition();
		if (od.getBodyTypeSelector() != null)
			return od.getBodyTypeSelector();
		else 
			return sd.getBodyTypeSelector();
	}
	
	private void setDefaultBodyType(ChenileExchange exchange) {
		OperationDefinition od = exchange.getOperationDefinition();
		if (od.getInput() != null) {
			TypeReference<?> ref = convertClassToTypeReference(od.getInput());
			exchange.setBodyType(ref);
		}
	}

	private TypeReference<?> convertClassToTypeReference(Class<?> clazz){
		return new TypeReference<Object>(){
			@Override
			public Type getType() {
				return clazz;
			}
		};
	}
}
