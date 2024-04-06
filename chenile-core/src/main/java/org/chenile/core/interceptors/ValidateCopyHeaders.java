package org.chenile.core.interceptors;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ContextContainer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Raja Shankar Kolluru
 * As per HTTP convention, all custom headers start with "x-"
 * This interceptor copies all  headers that start with "x-" into the context.
 * The context can then be used to read the values by subsequent interceptors and other classes.
 * If headers start with x-p- then they are considered protected. This means that
 * they cannot be passed from the outside by the requester. Attempts to pass
 * them will result in a security exception. This is because we consider such 
 * requests as attempts to hack the system. These internal headers will need to be pushed into the 
 * system by subsequent interceptors. 
 * Please see chenile headers for more details.
 *
 */
public class ValidateCopyHeaders extends BaseChenileInterceptor{
	@Autowired ContextContainer contextContainer;
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		for(String headerName: exchange.getHeaders().keySet()) {
			if (headerName.toLowerCase().startsWith("x-p-")) {
				throw new ErrorNumException(403,507, new Object[] {headerName});
			}else if (headerName.toLowerCase().startsWith("x-")){
				contextContainer.put(headerName,exchange.getHeader(headerName,String.class));
			}
		}
	}
	

}
