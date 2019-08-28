package org.chenile.core.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.entrypoint.ExceptionHandler;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
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
        List<Object> invokableParams=new ArrayList<>();
      
        for(ParamDefinition pd:op.getParams()){
            if(pd.getType().equals(HttpBindingType.BODY)){
                invokableParams.add(exchange.getBody());
            }else if(pd.getType().equals(HttpBindingType.HEADER)){
                invokableParams.add(convert(pd.getParamClass(),exchange.getHeader(pd.getName())));
            }
        }
    
        Object ret = method.invoke(targetBean, invokableParams.toArray());
        exchange.setResponse(ret);
    }
	
    private static Object convert( Class<?> clazz, Object v ) {
    	if (v.getClass().equals(clazz)) return v;
    	String value = null;
    	if (!( v instanceof String)) return v;
    	value = (String)v;
        if( boolean.class == clazz || Boolean.class == clazz ) return Boolean.parseBoolean( value );
        if( byte.class == clazz || Byte.class == clazz ) return Byte.parseByte( value );
        if( short.class == clazz || Short.class == clazz ) return Short.parseShort( value );
        if( int.class == clazz || Integer.class == clazz ) return Integer.parseInt( value );
        if( long.class == clazz || Long.class == clazz ) return Long.parseLong( value );
        if( float.class == clazz || Float.class == clazz ) return Float.parseFloat( value );
        if( double.class == clazz || Double.class == clazz ) return Double.parseDouble( value );
        throw new ServerException(ErrorCodes.CANNOT_INVOKE_TARGET.ordinal(),
        		"Cannot invoke argument for class type " + clazz.getName() + 
        		" in header when a string argument is passed");
    }
}
