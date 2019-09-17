package org.chenile.proxy.test.service;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

public class FooInterceptor extends BaseChenileInterceptor{

	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		FooModel fooModel = (FooModel)exchange.getBody();
		fooModel.setIncrement(fooModel.getIncrement()+1);
	}

}
