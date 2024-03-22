package org.chenile.stm.test;

import org.chenile.stm.model.Transition;

import junit.framework.TestCase;

public class TestTransition extends TestCase{
	Transition transition;
	public void setUp() throws Exception {
		transition = new Transition();
	}
	
	public void testAcls() {
		transition.setAclString("xxx,yyy");
		assertEquals("Number of ACLS dont match",2,transition.getAcls().length);
	}
}
