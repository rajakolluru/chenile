package org.chenile.cloudedgeswitch.test.service;

import java.util.HashMap;
import java.util.Map;

public class TestServiceImpl implements TestService {

	@Override
	public Map<String, Object> f1() {
		HashMap<String,Object> map = new HashMap<>();
		map.put("test", "test");
		return map;
	}

	@Override
	public Map<String, Object> f2(String c, ExamplePayload param2) {
		Map<String,Object> map = new HashMap<>();
		map.put("a",param2.a);
		map.put("b",param2.b);
		map.put("c",c);
		return map;
	}
}
