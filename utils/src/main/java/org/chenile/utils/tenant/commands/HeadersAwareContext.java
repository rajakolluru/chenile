package org.chenile.utils.tenant.commands;

import java.util.Map;

public interface HeadersAwareContext {
	public Map<String,Object> getHeaders();
}
