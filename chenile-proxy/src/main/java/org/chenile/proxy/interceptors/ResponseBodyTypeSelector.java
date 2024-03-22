package org.chenile.proxy.interceptors;

import org.chenile.base.response.GenericResponse;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.OperationDefinition;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

public class ResponseBodyTypeSelector extends BaseChenileInterceptor {

	@Override
	protected void doPreProcessing(ChenileExchange exchange) {		
		OperationDefinition od = exchange.getOperationDefinition();
		if (od.getOutput() != null) {
			ResolvableType rt = ResolvableType.forClassWithGenerics(GenericResponse.class, od.getOutput());
			ParameterizedTypeReference<?> ref = ParameterizedTypeReference.forType(rt.getType());
			exchange.setResponseBodyType(ref);
		}
	}
}
