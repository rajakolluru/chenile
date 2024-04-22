package org.chenile.http.test.service;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

public class JsonInterceptor extends BaseChenileInterceptor{
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		String s = exchange.getHeader(SOME_SPECIAL_HEADER,String.class);
		if (s != null && s.equals("throw-exception-pre-process")){
			JsonData j = (JsonData) exchange.getBody();
			if (j == null) return;
			throw new ServerException(j.getErrorNum(),j.getExceptionMessage());
		}
	}

	public static final String SOME_SPECIAL_HEADER = "some-special-header";
	public static final String SOME_CONSTANT = "some-constant";
	@Override
	protected void doPostProcessing(ChenileExchange exchange) {
		JsonData j = (JsonData)exchange.getResponse();
		if (j == null) return;
		String s = exchange.getHeader(SOME_SPECIAL_HEADER,String.class);
		if (s != null){
			if(s.equals("throw-exception-post-process")){
				throw new ServerException(j.getErrorNum(),j.getExceptionMessage())	;
			}else {
					j.setSomeSpecialHeaderValue(s + SOME_CONSTANT);
			}
		}
	}
}
