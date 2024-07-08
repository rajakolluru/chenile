package org.chenile.core.interceptors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.HttpBindingType;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;
import org.chenile.owiz.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * This command invokes a service gathering inputs from {@link ChenileExchange}.
 * Inserted at the end of the interception chain.
 */
public class ServiceInvoker implements Command<ChenileExchange>{

	private static final Logger LOG = LoggerFactory
			.getLogger(ServiceInvoker.class);
    @Override
	public void execute(ChenileExchange chenileExchange) {
		Throwable retException = null;
		String serviceOp = chenileExchange.getServiceDefinition().getName() + "." +
				chenileExchange.getOperationDefinition().getName();
		try {
			constructApiInvocation(chenileExchange);
			invokeApi(chenileExchange);
		} catch (InvocationTargetException e) {
			retException = e.getCause();
			logError(serviceOp, retException.getMessage(), retException.getClass().getName());
		} catch(Throwable e) {
			logError(serviceOp,e.getMessage(), e.getClass().getName());
			retException = e;
		}
		if (retException != null){
			chenileExchange.setException(surroundExceptionIfRequired(retException));
		}
	}

	private void logError(String serviceOp,String message, String className){
		LOG.info("Error while executing service {}. Message = {} (type = {})",serviceOp,
				message, className);
	}

	private ErrorNumException surroundExceptionIfRequired(Throwable e){
		if (e instanceof ErrorNumException errorNumException){
			return errorNumException;
		}
		return new ServerException(ErrorCodes.CANNOT_INVOKE_TARGET.getSubError(),
				new Object[] {e.getMessage()},e);
	}

	private void invokeApi(ChenileExchange exchange) throws IllegalAccessException, InvocationTargetException {
    	Object targetBean = exchange.getServiceReference();// exchange.getServiceDefinition().getServiceReference();	
    	Method method = exchange.getMethod();
        List<Object> apiInvocation = exchange.getApiInvocation();
    
        Object ret = method.invoke(targetBean, apiInvocation.toArray());
        exchange.setResponse(ret);
    }

	/**
	 * populates the apiInvocation for use by the invokeApi() call above
	 * @param exchange - the chenile exchange that is passed around
	 */
	private void constructApiInvocation(ChenileExchange exchange) {
		if (exchange.getApiInvocation() != null) return;
		OperationDefinition operationDefinition = exchange.getOperationDefinition();
		List<Object> invokableParams=new ArrayList<>();

		for(ParamDefinition pd:operationDefinition.getParams()){
			if(pd.getType().equals(HttpBindingType.BODY)){
				invokableParams.add(exchange.getBody());
			}else if(pd.getType().equals(HttpBindingType.HEADER)){
				invokableParams.add(convert(pd.getParamClass(),exchange.getHeader(pd.getName()),
						pd.getName()));
			}else if (pd.getType().equals(HttpBindingType.HEADERS)) {
				invokableParams.add(exchange.getHeaders());
			}else if (pd.getType().equals(HttpBindingType.MULTI_PART)) {
				invokableParams.add(extractMultipart(pd.getName(),exchange));
			}
		}
		exchange.setApiInvocation(invokableParams);
	}

	private static Object convert( Class<?> clazz, Object v, String paramName ) {
		if (v == null) return null;
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
		throw new ServerException(ErrorCodes.CANNOT_CONVERT_HEADER.getSubError(),
				new Object[]{paramName,clazz.getName()});
	}

	private static MultipartFile extractMultipart(String name, ChenileExchange exchange) {
		return exchange.getMultiPartMap().get(name);
	}

}
