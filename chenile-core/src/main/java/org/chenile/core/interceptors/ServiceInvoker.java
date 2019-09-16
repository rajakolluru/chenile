package org.chenile.core.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ExceptionHandler;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.Command;

/**
 * 
 * @author meratransport
 * An implementation of the ChenileInterceptor that can be used to invoke any service. 
 * Inserted at the end of the interception chain. 
 * 
 */
public class ServiceInvoker implements Command<ChenileExchange>{
    @Override
	public void execute(ChenileExchange chenileExchange) {		
		try {
			invokeApi(chenileExchange);
		} catch (IllegalAccessException e) {
			throw new ServerException(ErrorCodes.CANNOT_INVOKE_TARGET.getSubError(), 
					ErrorCodes.CANNOT_INVOKE_TARGET.name() + ": " + e.getMessage(),e);
		} catch (InvocationTargetException e) {
			ExceptionHandler.handleException(e);
		}
	}

	public void invokeApi(ChenileExchange exchange) throws IllegalAccessException, InvocationTargetException {
    	OperationDefinition op = exchange.getOperationDefinition();
    	Object targetBean = exchange.getServiceDefinition().getServiceReference();
    	Method method = op.getMethod();
        List<Object> apiInvocation = exchange.getApiInvocation();
    
        Object ret = method.invoke(targetBean, apiInvocation.toArray());
        exchange.setResponse(ret);
    }
}
