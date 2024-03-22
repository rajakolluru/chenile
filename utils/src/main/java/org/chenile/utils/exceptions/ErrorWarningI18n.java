package org.chenile.utils.exceptions;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

public class ErrorWarningI18n extends BaseChenileInterceptor{

	@Override
	protected void doPostProcessing(ChenileExchange exchange) {
		Object response = exchange.getResponse();
		// find the exceptions and ensure that the message in the interception is internationalized
		
	}

}
