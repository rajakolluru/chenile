package org.chenile.http.test.service;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

public class JsonInterceptor extends BaseChenileInterceptor{
	public static final String SOME_SPECIAL_HEADER = "some-special-header";
	public static final String SOME_CONSTANT = "some-constant";
	@Override
	protected void doPostProcessing(ChenileExchange exchange) {
		JsonData j = (JsonData)exchange.getResponse();
		if (j == null) return;
		String s = exchange.getHeader(SOME_SPECIAL_HEADER,String.class);
		if (s != null) {
			j.setSomeSpecialHeaderValue(s + SOME_CONSTANT);
		}
	}
}
