package org.chenile.stm.test.actionInfoProvider;

import org.chenile.stm.STMSecurityStrategy;

public class MockSS implements STMSecurityStrategy {
    public static String userName = "";
    @Override
    public boolean isAllowed(String... acls) {
        if (acls == null || acls.length == 0) return true;
        return userName.equals("finance") && acls[0].equals("finance-user");
    }
}