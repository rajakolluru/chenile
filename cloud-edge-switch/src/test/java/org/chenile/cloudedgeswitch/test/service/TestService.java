package org.chenile.cloudedgeswitch.test.service;

import java.util.Map;

public interface TestService {
	Map<String,Object> f1();
	Map<String,Object> f2(String c, ExamplePayload param2);
}
