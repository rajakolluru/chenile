package org.chenile.security.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserRolesStoreImpl implements UserRolesStore {
	
	private Map<String, Set<String>> userRoles = new HashMap<String, Set<String>>();

	/**
	 * @param userRoles the userRoles to set
	 */
	public void setUserRoles(Map<String, Set<String>> userRoles) {
		this.userRoles = userRoles;
	}

	@Override
	public Set<String> getRolesForUser(String userId) {
		return userRoles.get(userId);
	}

}
