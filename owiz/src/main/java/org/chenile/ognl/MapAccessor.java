package org.chenile.ognl;

import java.util.Map;

import ognl.MapPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;
import ognl.OgnlRuntime;

/**
 * Allows Ognl to support an object that is a map with a few properties defined like normal
 * java beans. The out of the box MapPropertyAccessor does not support java bean style access.
 * Ex: public class Foo extends HashMap {
 *    private String bar;
 *    // getters and setters for bar.
 *  }
 *  In this case, MapPropertyAccessor cannot set or get values to bar since it is defined as a 
 *  normal bean property. 
 *  This class helps out and leverages both the functionalities of the MapPropertyAccessor and
 *  ObjectPropertyAccessor. 
 * @author raja
 *
 */
public class MapAccessor extends MapPropertyAccessor {
	protected static ObjectPropertyAccessor opa;
	static{
		try {		
			opa = (ObjectPropertyAccessor)OgnlRuntime.getPropertyAccessor(Object.class);
		} catch (OgnlException e) {
			e.printStackTrace();
		}
	}
	
	public Object getProperty(Map context, Object target, Object name)
			throws OgnlException {
		Object value = super.getProperty(context, target, name);
		if (value == null)
			value = opa.getProperty(context, target, name);
		return value;
	}

	
	public void setProperty(Map context, Object target, Object name, Object value)
			throws OgnlException {
		try{
			opa.setProperty(context, target, name, value);
		}catch (Exception e) {
			super.setProperty(context, target, name, value);
		}
	}
}
