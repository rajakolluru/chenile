package org.chenile.core.interceptors;

import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;

/**
 * An object that prepares the {@link ChenileExchange} for invocation. 
 * It constructs the {@link ChenileExchange#getApiInvocation()} for use later
 * @author Raja Shankar Kolluru
 *
 */
public class ConstructApiInvocation extends BaseChenileInterceptor{

	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		if (exchange.getApiInvocation() != null) return;
		OperationDefinition operationDefinition = exchange.getOperationDefinition();
		List<Object> invokableParams=new ArrayList<>();
	      
        for(ParamDefinition pd:operationDefinition.getParams()){
            if(pd.getType().equals(HttpBindingType.BODY)){
                invokableParams.add(exchange.getBody());
            }else if(pd.getType().equals(HttpBindingType.HEADER)){
                invokableParams.add(convert(pd.getParamClass(),exchange.getHeader(pd.getName())));
            }
        }
        exchange.setApiInvocation(invokableParams);
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
