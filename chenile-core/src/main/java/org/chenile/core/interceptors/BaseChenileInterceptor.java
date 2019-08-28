package org.chenile.core.interceptors;

import org.chenile.core.context.ChenileExchange;
import org.chenile.owiz.Command;

public class BaseChenileInterceptor implements Command<ChenileExchange>{

	@Override
	public void execute(ChenileExchange exchange) throws Exception {
		doPreProcessing(exchange);
		doContinue(exchange);
		doPostProcessing(exchange);
	}
	
	protected void doPostProcessing(ChenileExchange exchange) {
		
	}

	protected void doPreProcessing(ChenileExchange exchange) {
		
		
	}

	protected void doContinue(ChenileExchange exchange) throws Exception{
		exchange.getChainContext().doContinue();
	}

}
