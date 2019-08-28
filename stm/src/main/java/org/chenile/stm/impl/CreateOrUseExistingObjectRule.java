package org.chenile.stm.impl;

import java.lang.reflect.Method;

import org.apache.commons.digester.ObjectCreateRule;
import org.xml.sax.Attributes;
/**
 * Useful as an enhancement over the ObjectCreateRule for digester. This one creates a new object or uses an existing one if an old one
 * was already created using the same ID. The ID has to be an attribute for the given xml tag. Its name is "id"  by default 
 * but can be changed to something else if required. The finderMethod is 
 * the method to call on the previous object in the digester stack ( the object that is assumed to "collect" all the IDs so far) 
 * to obtain the
 * old object if an old object was already created for that Id. The finderMethod must accept the "id" and nothing else and return the
 * already created object. 
 * Only string ids are supported.
 * @author Raja Shankar Kolluru
 *
 * @param <T>
 */
public class CreateOrUseExistingObjectRule <T> extends ObjectCreateRule{
	private static final String ID = "id";
	private String finderMethod;
	private Class<T> classToCreate;
	private String idAttributeName = ID;
	
	public CreateOrUseExistingObjectRule(Class<T> whichClazz, String finderMethod,String idAttributeName) {
		super(whichClazz);
		this.classToCreate = whichClazz;
		this.finderMethod = finderMethod;
		this.idAttributeName = idAttributeName;
	}
	
	public CreateOrUseExistingObjectRule(Class<T> whichClazz, String finderMethod) {
		super(whichClazz);
		this.classToCreate = whichClazz;
		this.finderMethod = finderMethod;
	}

	@SuppressWarnings("unchecked")
	public void begin(Attributes attributes) throws Exception {
		String id = attributes.getValue(idAttributeName);
		Object parent = digester.peek();
		Method method = parent.getClass().getMethod(finderMethod,String.class);
		T instance = null;
		if (id != null && method != null) instance = (T)method.invoke(parent,id);
		if (instance == null) instance = classToCreate.newInstance();
		digester.push(instance);
		
	}
	

}
