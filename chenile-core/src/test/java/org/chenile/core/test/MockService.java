package org.chenile.core.test;

import org.chenile.base.exception.ServerException;
import java.util.List;

public class MockService {
	public List<String> mockMethod(List<String> list) {
		list.add("actual");
		return list;
	}
	
	public String s1(String id) {
		return "mock" + id;
	}
	
	public String s2(Integer id) {
		return "mockint" + id;
	}
	
	public int s3(int id) {
		return 43 + id;
	}
	
	public boolean s4(boolean flag) {
		return !flag;
	}
	
	public String s5(int exceptionNum) {
		throw new ServerException(exceptionNum,"Error");
	}
	
	public Object s6(String eventId, Object param) {
		// ensure that for eventId = "e1" the param is of type E1
		// and for event Id  = "e2" the param is of type E2.
		// the casts below wont work if that is not the case
		if (eventId.equals("e1")) {
			E1 e1 = (E1) param;
			return e1;
		}else if (eventId.equals("e2")) {
			E2 e2 = (E2) param;
			return e2;
		}
		return null;
	}
}
