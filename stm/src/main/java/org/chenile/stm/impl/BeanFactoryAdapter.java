package org.chenile.stm.impl;

public interface BeanFactoryAdapter {
	/**
	 * Supports the notion of bean factories.
	 * @param componentName the component to look for in the bean factory.
	 * @return
	 */
	public Object getBean(String componentName);
}
