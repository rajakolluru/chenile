package org.chenile.core.test;

public class T1MockService extends MockService{

	@Override
	public String s1(String id) {
		return "t1mock" + id;
	}

}
