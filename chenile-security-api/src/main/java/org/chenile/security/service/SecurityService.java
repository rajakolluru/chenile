package org.chenile.security.service;

import org.chenile.core.context.ChenileExchange;

public interface SecurityService {
	public String[] getCurrentAuthorities();
	public boolean doesCurrentUserHaveGuardingAuthorities(ChenileExchange exchange);
}
