package org.chenile.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;

public abstract class MethodUtils {
	public static Method computeMethod(String id, Object ref, OperationDefinition od) {
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		for (ParamDefinition pd: od.getParams()) {
			paramTypes.add(pd.getParamClass());
		}
		Class<?>[] parameterTypes = new Class<?>[paramTypes.size()];
    	try {
    		Method method = ref.getClass().getMethod(od.getMethodName(), 
    			paramTypes.toArray(parameterTypes));
    		return method;
    	}catch(NoSuchMethodException e) {
    		throw new ServerException(ErrorCodes.MISCONFIGURATION.ordinal(),
    				"Operation " + id + "." + od.getName() + "(" + paramTypes 
    				+ ") is not found. Did you define the paramClass properly?",e);
    	}
		
	}
}
