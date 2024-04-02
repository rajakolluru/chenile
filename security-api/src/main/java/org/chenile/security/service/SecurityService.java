package org.chenile.security.service;

public interface SecurityService {
	public boolean isAllowed(String userId, String[] acls);
}
