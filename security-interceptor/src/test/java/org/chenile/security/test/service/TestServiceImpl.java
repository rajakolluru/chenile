package org.chenile.security.test.service;

import java.util.HashMap;
import java.util.Map;

public class TestServiceImpl implements TestService {

	@Override
	public Map<String, Object> test() {
		return test1("test");
	}

	@Override
	public Map<String, Object> test1(String option) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("test", option);
		return map;
	}

	@Override
	public Map<String, Object> test2(String option) {
		return test1(option);
	}
}
