package org.chenile.stm.spring;

import org.chenile.stm.impl.BeanFactoryAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class SpringBeanFactoryAdapter implements BeanFactoryAdapter{
	@Autowired ApplicationContext applicationContext;

	@Override
	public Object getBean(String componentName) {
		return applicationContext.getBean(componentName);
	}
}
