package org.chenile.http.test;

import java.util.Map;

import org.chenile.http.handler.HttpEntryPoint;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("unittest")
public class TestExtractPathVarsInHttpEntryPoint {
	
	@Test public void testPathVars1() {
		Map<String, Object> m = HttpEntryPoint.extractPathVariables("/xxx/{id}", "/xxx/abc");
		assertMapContains(m,"id", "abc");
	}
	
	@Test public void testPathVars2() {
		Map<String, Object> m = HttpEntryPoint.extractPathVariables("/xxx/{id}/{name}", "/xxx/abc/def");
		assertMapContains(m,"id", "abc");
		assertMapContains(m,"name", "def");
	}
	
	@Test public void testPathVars3() {
		Map<String, Object> m = HttpEntryPoint.extractPathVariables("/xxx/{id}/{name}/", "/xxx/abc/def/");
		assertMapContains(m,"id", "abc");
		assertMapContains(m,"name", "def");
	}
	
	@Test public void testPathVars4() {
		Map<String, Object> m = HttpEntryPoint.extractPathVariables("/xxx/{id}/{name}/", "/xxx/abc/def");
		assertMapContains(m,"id", "abc");
		assertMapContains(m,"name", "def");
	}
	private void assertMapContains(Map<String, Object> m, String key, String value) {
		Assert.assertNotNull(m);
		Assert.assertTrue(m.containsKey(key));
		Assert.assertEquals(value, m.get(key));
	}
}
