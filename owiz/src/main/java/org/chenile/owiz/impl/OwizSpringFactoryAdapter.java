package org.chenile.owiz.impl;


import org.chenile.owiz.BeanFactoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Links OWIZ to the Spring framework. It looks up a command by name from the Spring bean factory
 */
public class OwizSpringFactoryAdapter implements BeanFactoryAdapter {
	
	@Autowired ApplicationContext applicationContext;
	public Object lookup(String componentName) {
		try {
			return applicationContext.getBean(componentName);
		}catch(Exception e) {
			return null;
		}
	}

}
