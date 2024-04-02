package org.chenile.security.service.impl;

import java.util.Set;

public interface RoleAclsStore {
	public Set<String> getAclsForRole(String roleId);
}
