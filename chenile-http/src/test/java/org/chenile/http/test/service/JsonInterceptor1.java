package org.chenile.http.test.service;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

/**
 * A variant of JSonInterceptor to test a situation where two
 * interceptors throw exceptions in their post process methods.
 * This is inserted only for the throwDoubleExceptions method in JsonService.
 */
public class JsonInterceptor1 extends BaseChenileInterceptor{

	@Override
	protected void doPostProcessing(ChenileExchange exchange) {
		JsonData data = (JsonData) exchange.getResponse();
		if (data == null) return;
		// unconditionally throw an exception.
		throw new ErrorNumException(501,data.getErrorNum() + 1,data.getExceptionMessage() +"1");
	}
}
