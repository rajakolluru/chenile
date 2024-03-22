package org.chenile.query.service;

import java.util.Map;

public interface EntityQueryService {
	public Map<String,Object> query(Map<String,Object> allHeaders,Map<String,Object> request);
}
