package org.chenile.owiz.impl;

import java.lang.reflect.Method;

import org.chenile.owiz.BeanFactoryAdapter;

/**
 * This Command delegates control to a method of an underlying object whose reference is
 * passed to this command. This can be useful for trivial delegations.
 * @param <InputType>
 */
public class DelegatorCommand<InputType> extends CommandBase<InputType> {
	private BeanFactoryAdapter beanFactoryAdapter;
	public BeanFactoryAdapter getBeanFactoryAdapter() {
		return beanFactoryAdapter;
	}
	public void setBeanFactoryAdapter(BeanFactoryAdapter beanFactoryAdapter) {
		this.beanFactoryAdapter = beanFactoryAdapter;
	}

	private String methodName;
	private Object underlyingObject;
	private static final String UNDERLYING_OBJECT = "underlyingObject";
	private static final String METHOD_NAME = "method";
	
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object getUnderlyingObject() {
		return underlyingObject;
	}
	public void setUnderlyingObject(Object underlyingObject) {
		this.underlyingObject = underlyingObject;
	}
	
	
	@Override
	protected void doExecute(InputType context) throws Exception {
		Object delegateObject = underlyingObject;
		
		String meth = this.methodName;
		
		String underlyingCommand = commandDescriptor.getPropertyValue(UNDERLYING_OBJECT);
		if (underlyingCommand != null)
			delegateObject = beanFactoryAdapter.lookup(underlyingCommand);
		String methodName = commandDescriptor.getPropertyValue(METHOD_NAME);
		if (methodName != null)
			meth = methodName;

		Method m = getMethod(delegateObject,meth, context);
		m.invoke(delegateObject, context);
	}
	
	/**
	 * we don't know what is the signature since the arg type is unknown. So find the first one
	* arg method we assume that is the one.
	 * @param delegateObject .
	 * @param meth .
	 * @param context .
	 * @return .
	 */
	protected Method getMethod(Object delegateObject,String meth,InputType context){
	
		for(Method m : delegateObject.getClass().getMethods()){
			if(m.getName().equals(meth) && m.getParameterTypes().length == 1 && 
					m.getParameterTypes()[0].isInstance(context))
				return m;			
		}
		return null;
	}

}
