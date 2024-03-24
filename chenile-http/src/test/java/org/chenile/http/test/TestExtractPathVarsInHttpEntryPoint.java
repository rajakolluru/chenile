package org.chenile.http.test;

import java.util.Map;

import org.chenile.http.handler.HttpEntryPoint;
import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("unittest")
public class TestExtractPathVarsInHttpEntryPoint {
	
	public static void main(String[] args) {
		func("/xxx/{id}", "/xxx/abc");
		func("/xxx/{id}/{name}", "/xxx/abc/def");
		func("/xxx/{id}/{name}/","/xxx/abc/def/");
		func("/xxx/{id}/{name}/","/xxx/abc/def");
	}
	public static void func(String s1, String s2) {
		System.out.println("Trying for " + s1 + " and " + s2);
		// extractPathVariables(s1,s2);
	}
	@Test void testPathVars1() {
		Map<String, Object> m = HttpEntryPoint.extractPathVariables("/xxx/{id}", "/xxx/abc");
		assertMapContains(m,"id", "abc");
	}
	private void assertMapContains(Map<String, Object> m, String string, String string2) {
		
		
	}
}
