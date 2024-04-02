package org.chenile.query.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.chenile.security.service.impl.RoleAclsStore;
import org.chenile.security.service.impl.RoleAclsStoreImpl;
import org.chenile.security.service.impl.UserRolesStore;
import org.chenile.security.service.impl.UserRolesStoreImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Acts like Spring
 * @author Raja Shankar Kolluru
 *
 */
public class SecuritySettings {
	@Autowired UserRolesStore userRolesStore;
	@Autowired RoleAclsStore roleAclsStore;
	
	public void initializeMockRolesAcls() throws Exception{		
		// MERA-USER => [ admin , power-user ]
		// KAM-USER => [ kam ]
		// admin => [ INDENT-REJECT, INDENT-CONFIRM ]
		// kam => [ INDENT-CONFIRM ]
		// power-user => [] 
		
		Map<String, Set<String>> userRoles = new HashMap<String, Set<String>>();
		Map<String, Set<String>> roleAcls = new HashMap<String, Set<String>>();
		Set<String> meraRoles = new HashSet<String>();
		meraRoles.add("admin"); meraRoles.add("power-user");
		userRoles.put("MERA", meraRoles);
		Set<String> kamRoles = new HashSet<String>();
		kamRoles.add("kam");
		userRoles.put("MERA-USER", meraRoles);
		userRoles.put("KAM-USER", kamRoles);
		
		Set<String> kamAcls = new HashSet<String>();
		kamAcls.add("INDENT-CONFIRM");
		roleAcls.put("kam", kamAcls);
		
		Set<String> adminAcls = new HashSet<String>();
		adminAcls.add("INDENT-CONFIRM");adminAcls.add("INDENT-REJECT");
		roleAcls.put("admin", adminAcls);
		roleAcls.put("power-user",new HashSet<String>());
		
		((UserRolesStoreImpl) userRolesStore).setUserRoles(userRoles);
		((RoleAclsStoreImpl)roleAclsStore).setRoleAcls(roleAcls);
	}
}
