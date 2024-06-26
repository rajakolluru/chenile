package org.chenile.security.test.service;

import java.util.HashMap;
import java.util.Map;

public class TestServiceImpl implements TestService {

	@Override
	public Map<String, Object> normal() {
		return premium();
	}

	@Override
	public Map<String, Object> premium() {
		return selectivelyPremium("test");
	}

	@Override
	public Map<String, Object> selectivelyPremium(String option) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("test", option);
		return map;
	}

	@Override
	public Map<String, Object> selectivelyPremium1(String option) {
		return selectivelyPremium(option);
	}
}
