package org.chenile.samples.security.test.service;

import java.util.HashMap;
import java.util.Map;

public class TestServiceImpl implements TestService {

	@Override
	public Map<String, Object> example() {
		HashMap<String,Object> map = new HashMap<>();
		map.put("test", "test");
		return map;
	}
}
