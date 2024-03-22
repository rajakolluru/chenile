package org.chenile.core.test;

/**
 * Demonstrates the ability to run the mock service separately if required
 * @author Raja Shankar Kolluru
 *
 */

public class MockMockService extends MockService{

	@Override
	public String s1(String id) {
		return "mockmock" + id;
	}

}
