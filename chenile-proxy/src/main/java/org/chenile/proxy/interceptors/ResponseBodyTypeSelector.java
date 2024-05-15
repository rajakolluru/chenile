package org.chenile.proxy.interceptors;

import org.chenile.base.response.GenericResponse;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.OperationDefinition;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

/**
 * This class calculates the correct type for the response body. It will be of type GenericResponse<T> where
 * T is the return type of the underlying service. The underlying service return type can be represented as
 * a ParameterizedType of as a Class object, The treatment differs depending on what got specified
 */
public class ResponseBodyTypeSelector extends BaseChenileInterceptor {

	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		OperationDefinition od = exchange.getOperationDefinition();
		if (od.getOutputAsParameterizedReference() != null){
			ParameterizedTypeReference<?> ref = od.getOutputAsParameterizedReference();
			ResolvableType rt1 = ResolvableType.forType(ref);
			ResolvableType rt = ResolvableType.forClassWithGenerics(GenericResponse.class, rt1);
			ref = ParameterizedTypeReference.forType(rt.getType());
			exchange.setResponseBodyType(ref);
		}
		else if (od.getOutput() != null) {
			ResolvableType rt = ResolvableType.forClassWithGenerics(GenericResponse.class, od.getOutput());
			ParameterizedTypeReference<?> ref = ParameterizedTypeReference.forType(rt.getType());
			exchange.setResponseBodyType(ref);
		}
	}
}
