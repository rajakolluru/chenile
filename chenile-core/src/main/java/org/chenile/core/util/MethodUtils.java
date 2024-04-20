package org.chenile.core.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.chenile.base.exception.ServerException;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.OperationDefinition;
import org.chenile.core.model.ParamDefinition;

public abstract class MethodUtils {
	public static Method computeMethod( Class<?> clazz, OperationDefinition od) {
		List<Class<?>> paramTypes = new ArrayList<Class<?>>();
		for (ParamDefinition pd: od.getParams()) {
			paramTypes.add(pd.getParamClass());
		}
		Class<?>[] parameterTypes = new Class<?>[paramTypes.size()];
    	try {
    		return clazz.getMethod(od.getMethodName(),
    			paramTypes.toArray(parameterTypes));
    	}catch(NoSuchMethodException e) {
    		return null;
    	}
		
	}
}
