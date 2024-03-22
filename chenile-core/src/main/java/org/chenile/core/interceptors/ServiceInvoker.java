package org.chenile.core.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.owiz.Command;

/**
 * 
 * @author Raja Shankar Kolluru
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
			//e.printStackTrace();
			throw new ServerException(ErrorCodes.CANNOT_INVOKE_TARGET.getSubError(), 
					ErrorCodes.CANNOT_INVOKE_TARGET.name() + ": " + e.getMessage(),e);
		} catch (InvocationTargetException e) {
			//e.printStackTrace();
			chenileExchange.setException(e.getCause());	
		}catch(Throwable e) {
			//e.printStackTrace();
			throw new ServerException(ErrorCodes.CANNOT_INVOKE_TARGET.getSubError(), 
					ErrorCodes.CANNOT_INVOKE_TARGET.name() + ": " + e.getMessage(),e);
		}
	}

	public void invokeApi(ChenileExchange exchange) throws IllegalAccessException, InvocationTargetException {
    	Object targetBean = exchange.getServiceReference();// exchange.getServiceDefinition().getServiceReference();	
    	Method method = exchange.getMethod();
        List<Object> apiInvocation = exchange.getApiInvocation();
    
        Object ret = method.invoke(targetBean, apiInvocation.toArray());
        exchange.setResponse(ret);
    }
}
