package org.chenile.security.service.impl;

import org.chenile.security.service.SecurityService;
import org.chenile.stm.STMSecurityStrategy;
import org.springframework.beans.factory.annotation.Autowired;

public class StmSecurityStrategyImpl implements STMSecurityStrategy {
    @Autowired
    SecurityService securityService;
    @Override
    public boolean isAllowed(String... acls) {
        return securityService.doesCurrentUserHaveGuardingAuthorities(acls);
    }
}
