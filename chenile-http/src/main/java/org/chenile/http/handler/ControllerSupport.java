package org.chenile.http.handler;

import jakarta.servlet.http.HttpServletRequest;
import org.chenile.base.response.ResponseMessage;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.ChenileExchangeBuilder;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.entrypoint.ChenileEntryPoint;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.chenile.http.Constants;
import org.chenile.http.annotation.ChenileController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Arrays;
import java.util.List;

/**
 * This class must be extended by Chenile controllers who use annotation to configure a service.
 * Merely, call the process() method for delegating control to Chenile.
 * <p>The process() method constructs a ResponseEntity for the response returned by the Chenile Entry point</p>
 * @author Raja Shankar Kolluru
 *
 */
public class ControllerSupport {
	@Autowired ChenileExchangeBuilder chenileExchangeBuilder;
	@Autowired ChenileEntryPoint chenileEntryPoint;
	private final LocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
	protected String service;	
	public ControllerSupport() {
		ChenileController cc = this.getClass().getAnnotation(ChenileController.class);
		if (cc != null) {
			service = cc.value();
		}		
	}
	
	@SuppressWarnings("unchecked")
	protected<T> ResponseEntity<T> process(String opName, 
			HttpServletRequest request,Object...args ){
		ChenileExchange chenileExchange = makeExchange(request,opName,args);
		chenileExchange.setHeader(HeaderUtils.ENTRY_POINT, Constants.HTTP_ENTRY_POINT);
		chenileExchange.setApiInvocation(Arrays.asList(args));
		chenileEntryPoint.execute(chenileExchange);
		T response = (T) chenileExchange.getResponse();
		BodyBuilder bodyBuilder = ResponseEntity.status(chenileExchange.getHttpResponseStatusCode());
		enhanceBodyWithWarnings(bodyBuilder,chenileExchange);
		return bodyBuilder.body(response);	
	}

	protected<T> ResponseEntity<T> process(HttpServletRequest request,Object...args ) {
		StackTraceElement[] ste = Thread.currentThread().getStackTrace();
		assert ste.length >= 3;
		return process(ste[2].getMethodName(), request,args);
	}
	
	private void enhanceBodyWithWarnings(BodyBuilder bodyBuilder, ChenileExchange chenileExchange) {
		List<ResponseMessage>x =  chenileExchange.getResponseMessages();
		if (x == null) return;		
		for (ResponseMessage message: x) {
			bodyBuilder.header(HttpHeaders.WARNING,message.getDescription());
		}	
	}
	
	private ChenileExchange makeExchange(HttpServletRequest request, String opName,Object[] args) {
		ChenileExchange chenileExchange = chenileExchangeBuilder.makeExchange(service, opName, null);
		chenileExchange.setHeaders(HttpEntryPoint.getHeaders(chenileExchange.getOperationDefinition(),request));
		chenileExchange.setLocale(localeResolver.resolveLocale(request));
		chenileExchange.setMultiPartMap(HttpEntryPoint.getMultiPartMap(request));
		// populate body from the RequestBody param
		populateBody(chenileExchange,args);
		return chenileExchange;
	}
	
	private void populateBody(ChenileExchange chenileExchange,Object[] args) {
		OperationDefinition od = chenileExchange.getOperationDefinition();
		for(int i =0; i< od.getParams().size();i++) {
			ParamDefinition pd = od.getParams().get(i);
			if (pd.getType().equals(HttpBindingType.BODY)) {
				chenileExchange.setBody(args[i]);
			}			
		}		
	}
}
