package org.chenile.security.test.service;

import java.util.Map;

public interface TestService {
	public Map<String,Object> premium();
	public Map<String,Object> selectivelyPremium(String option);
	public Map<String,Object> selectivelyPremium1(String option);
	public Map<String,Object> normal();
}
