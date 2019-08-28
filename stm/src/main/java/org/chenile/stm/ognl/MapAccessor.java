package org.chenile.stm.ognl;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	
	private final static Logger LOGGER = Logger.getLogger(MapAccessor.class.getName());

	
	protected static ObjectPropertyAccessor opa;
	static{
		try {		
			opa = (ObjectPropertyAccessor)OgnlRuntime.getPropertyAccessor(Object.class);
		} catch (OgnlException e) {
			LOGGER.log(Level.WARNING, "ObjectPropertyAccessor", e);
		}
	}
	
	/**
	 * 
	 */
	public Object getProperty(Map context, Object target, Object name)
			throws OgnlException {
		Object value = super.getProperty(context, target, name);
		if (value == null)
			value = opa.getProperty(context, target, name);
		return value;
	}

	/**
	 * Map context, 
	 * Object target, Object name, Object value
	 */
	public void setProperty(Map context, Object target, Object name, Object value)
			throws OgnlException {
		try{
			opa.setProperty(context, target, name, value);
		}catch (Exception e) {
			LOGGER.log(Level.WARNING, "setProperty", e);
			super.setProperty(context, target, name, value);
		}
	}
}
