package org.chenile.stm.dummy;

import org.chenile.stm.STMSecurityStrategy;
import org.chenile.stm.StateEntity;

public class DummySecurityStrategy implements STMSecurityStrategy {
    @Override
    public boolean isAllowed(String... acls) {
        return false;
    }
}
