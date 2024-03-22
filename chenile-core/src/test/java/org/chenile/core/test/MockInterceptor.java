package org.chenile.core.test;

import java.util.List;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

public class MockInterceptor extends BaseChenileInterceptor{

	private String prefix;
	private String suffix;

	MockInterceptor(String prefix,String suffix){
		this.prefix = prefix;
		this.suffix = suffix;
	}
	
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		Object body = exchange.getBody();
		if(!(body instanceof List<?>))
			return ;
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>)exchange.getBody();		
		list.add(prefix);
	}

	@Override
	protected void doPostProcessing(ChenileExchange exchange) {
		Object body = exchange.getResponse();
		if(!(body instanceof List<?>))
			return ;
		@SuppressWarnings("unchecked")
		List<String> list = (List<String>)exchange.getResponse();
		list.add(suffix);
	}

}
