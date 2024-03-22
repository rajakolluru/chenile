package org.chenile.cucumber;

import java.util.HashMap;
import java.util.Map;

public enum CukesContext {

	CONTEXT;

	public ThreadLocal<Map<String, Object>> c = new ThreadLocal<>();

	CukesContext() {
		c.set(new HashMap<String, Object>());
	}

	public Map<String, Object> context() {
		return c.get();
	}

	public void set(String key, Object value) {
		context().put(key, value);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) context().get(key);
	}

	public void reset() {
		context().clear();
	}

}

