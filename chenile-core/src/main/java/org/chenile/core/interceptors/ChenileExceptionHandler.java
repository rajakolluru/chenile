package org.chenile.core.interceptors;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.response.ResponseMessage;
import org.chenile.base.response.WarningAware;
import org.chenile.core.context.ChenileExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.List;
import java.util.Locale;

/**
 * Handles all exceptions by:
 * Translating all errors and warnings to international language. (service is assumed to throw just error code and not error message)
 * 
 * This can be extended to provide custom functionality.
 * But be sure that the extended exception handler is instantiated in a spring configuration and the 
 * name of the spring configuration must be set in chenile.exception.handler in "chenile.properties"
 * @author Raja Shankar Kolluru
 *
 */
public class ChenileExceptionHandler extends BaseChenileInterceptor{

	@Autowired MessageSource messageSource;
	@Override
	protected void doPostProcessing(ChenileExchange exchange) {
		translateErrors(exchange);
		translateWarnings(exchange);
	}
	
	
	protected void translateErrors(ChenileExchange exchange) {
		RuntimeException e = exchange.getException();
		if (e == null) return;
		if (!(e instanceof ErrorNumException))
			return;
		ErrorNumException ene = (ErrorNumException)e;
		if (ene.getMessage() != null)
			return;
		ene.setMessage(translate(ene.getSubErrorNum(),ene.getParams(),exchange.getLocale()));
	}
	
	protected void translateWarnings(ChenileExchange exchange) {
		Object o = exchange.getResponse();
		List<ResponseMessage> warningMessages = WarningAware.obtainWarnings(o);
		if(warningMessages == null) {
			return;
		}
		for(ResponseMessage m: warningMessages) {
			if (m.getDescription() != null)
				continue;
			m.setDescription(translate(m.getSubErrorCode(),m.getParams(),exchange.getLocale()));
		}
	}
	
	private String translate(int code,Object[] params,Locale locale) {
		String defaultMessage = "Message code " + code + " not found in resource bundle";
		
		try {
			String m = messageSource.getMessage("E" + code,params,defaultMessage,locale);
			if (m == null) m = defaultMessage;
			return m;
		}catch(Exception e) {
			return defaultMessage;
		}
	}
}
