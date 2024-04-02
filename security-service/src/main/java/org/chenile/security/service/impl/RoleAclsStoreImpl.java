package org.chenile.security.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RoleAclsStoreImpl implements RoleAclsStore {
	
	private Map<String, Set<String>> roleAcls = new HashMap<String, Set<String>>();

	/**
	 * @param userRoles the userRoles to set
	 */
	public void setRoleAcls(Map<String, Set<String>> roleAcls) {
		this.roleAcls = roleAcls;
	}


	@Override
	public Set<String> getAclsForRole(String roleId) {
		return roleAcls.get(roleId);
	}

}
