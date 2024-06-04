package org.chenile.cucumber;

import java.util.HashMap;
import java.util.Map;

/**
 * A singleton object that is used in all the methods to store and retrieve values <br/>
 * Cucumber recommends an enum since it acts like a perfect singleton. We could have also
 * used a static instance as an alternative.
 *
 */
public enum CukesContext {

	CONTEXT;

	public ThreadLocal<Map<String, Object>> c = new ThreadLocal<>();

	/**
	 * Pushes a hash map into the thread local so that we can now use the hash map to
	 * store and retrieve stuff
	 */
	CukesContext() {
		c.set(new HashMap<String, Object>());
	}

	/**
	 * This uses a Thread Local to set and retrieve values
	 * @return a map of all the current values stored within the thread.
	 */
	public Map<String, Object> context() {
		return c.get();
	}

	/**
	 * A convenience method that retrieves the thread local and stores stuff into it.
	 * @param key the key to store against
	 * @param value the value to store in the key
	 */
	public void set(String key, Object value) {
		context().put(key, value);
	}

	/**
	 * A convenience method that retrieves the thread local and retrieves values against keys
	 * @param key the key to use
	 * @return the value stored in the key
	 * @param <T> this avoids the need to cast the results.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) context().get(key);
	}

	/**
	 * Resets the map. Happens at the end of every scenario.
	 */
	public void reset() {
		context().clear();
	}

}

