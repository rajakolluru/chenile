package org.chenile.security.service;

import org.chenile.core.context.ChenileExchange;

public interface SecurityConfigService {
    public String[] getGuardingAuthorities(ChenileExchange exchange);
}
