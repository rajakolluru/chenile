package org.chenile.owiz;

/**
 * Interface defines a contract for classes that look up objects from various DI frameworks (aka Bean factories)
 * @author rkollu
 *
 */
public interface BeanFactoryAdapter {
	/**
	 * 
	 * @param componentName - the name of the instance as configured in the bean factory.
	 * @return the actual instance created from the bean factory. This would benefit from dependency injection.
	 */
	public Object lookup(String componentName);
}
