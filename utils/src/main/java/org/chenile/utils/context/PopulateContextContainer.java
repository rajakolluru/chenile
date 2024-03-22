package org.chenile.utils.context;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderCopier;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.utils.context.ContextContainer.SimpleMap;
import org.springframework.beans.factory.annotation.Autowired;

public class PopulateContextContainer extends BaseChenileInterceptor implements HeaderCopier{

	@Autowired ContextContainer contextContainer;
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		// populate the context container from ChenileExchange
		populateContextFromChenileExchange(exchange,contextContainer);
	}
	
	public static void populateContextFromChenileExchange(ChenileExchange exchange,ContextContainer contextContainer) {
			contextContainer.fromSimpleMap(new SimpleMap() {
				public String getValue(String key) {
					return exchange.getHeader(key,String.class);
				}
			});
	}
	
	public static void populateChenileExchangeFromContext(ChenileExchange exchange, ContextContainer contextContainer) {
		contextContainer.toMap().forEach((k,v)->{
			exchange.setHeader(k,v);
		});
	}

	@Override
	public void copy(ChenileExchange exchange) {
		populateChenileExchangeFromContext(exchange, contextContainer);		
	}

}
