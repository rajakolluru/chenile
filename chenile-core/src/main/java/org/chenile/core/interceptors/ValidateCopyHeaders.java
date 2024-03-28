package org.chenile.core.interceptors;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ContextContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author Raja Shankar Kolluru
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
