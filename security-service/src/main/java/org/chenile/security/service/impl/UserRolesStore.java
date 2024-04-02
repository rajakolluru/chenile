package org.chenile.security.service.impl;

import java.util.Set;

public interface UserRolesStore {
	public Set<String> getRolesForUser(String userId);
}
