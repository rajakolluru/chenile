package org.chenile.foo.test.service;

import java.util.HashMap;
import java.util.Map;

public class TestServiceImpl1 implements TestService{

	@Override
	public Map<String, Object> example() {
		HashMap<String,Object> map = new HashMap<>();
		map.put("test", "test1");
		return map;
	}
	
	public String toString() {
		return "testServiceImpl1";
	}
}
