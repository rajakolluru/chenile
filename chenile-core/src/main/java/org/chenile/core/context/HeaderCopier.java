package org.chenile.core.context;

/**
 * Any class that copies headers from the existing context to the new {@link ChenileExchange} 
 * @author Raja Shankar Kolluru
 *
 */
public interface HeaderCopier {
	public void copy(ChenileExchange exchange);
}
