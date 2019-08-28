package org.chenile.stm.test.basicflow;

import org.chenile.stm.STMSecurityStrategy;

public class MockSecurityStrategy implements STMSecurityStrategy {
	public static ThreadLocal<String>  tl = new ThreadLocal<String>();
	
	@Override
	public boolean isAllowed(String... acls) {
		
		String currentUser = tl.get();
		if (currentUser != null && currentUser.equals("ValidUser"))
			return true;
		
		return false;
	}

}
