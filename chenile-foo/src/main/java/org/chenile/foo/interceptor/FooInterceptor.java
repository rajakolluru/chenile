package org.chenile.foo.interceptor;

import java.util.Map;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;

/**
 * Caches an operation return value using the service name, operation name and the parameters
 * that were passed to the operation. 
 * This is applicable only to operations that subscribe to caching.
 * @author Raja Shankar Kolluru
 *
 */
public class FooInterceptor extends BaseChenileInterceptor {

	@SuppressWarnings("unchecked")
	@Override
	protected void doPostProcessing(ChenileExchange exchange) {
		Map<String,Object> foc = getExtensionByAnnotation("Foo", exchange);
		Map<String,Object> map = (Map<String,Object>)exchange.getResponse();
		map.put("foo", foc.get("message"));
	}

	@Override
	protected boolean bypassInterception(ChenileExchange exchange) {
		Map<String,Object> foc = getExtensionByAnnotation("Foo", exchange);
		if(foc != null)return false;
		return true;
	} 
	
	
}
