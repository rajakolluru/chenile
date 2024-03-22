package com.chenile.cache;

import java.io.Serializable;

/**
 * Implement this interface if you want to create your own custom key out of the 
 * input params passed to a method.
 * @author Raja Shankar Kolluru
 *
 */
public interface Cacheable {
	public Serializable cacheKey();
}
