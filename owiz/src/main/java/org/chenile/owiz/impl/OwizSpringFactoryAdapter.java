package org.chenile.owiz.impl;


import org.chenile.owiz.BeanFactoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

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
