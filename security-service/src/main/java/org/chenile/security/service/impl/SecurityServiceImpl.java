package org.chenile.security.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.chenile.security.model.Acl;
import org.chenile.security.model.EmployeeDetails;
import org.chenile.security.model.RoleAcl;
import org.chenile.security.service.ProfileServiceContextUtil;
import org.chenile.security.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;


public class SecurityServiceImpl implements SecurityService{
	@Autowired
	@Lazy
	private ProfileServiceContextUtil profileServiceContextUtil;

	/**
	 * Returns true if the user passed is allowed to access any of the ACLs
	 */
	public boolean isAllowed(String userId,String[] acls) {
		if (acls == null || acls.length == 0) return true;
		EmployeeDetails employeeDetails = profileServiceContextUtil.getEmployeeDetails();
		if (null == employeeDetails) return false;

		Set<String> allAcls = new HashSet<String>();
		
		List<RoleAcl> rolesAcls = employeeDetails.getRoles();
		if (null == rolesAcls) return false;
		List<Acl> aclList = new ArrayList<>();
		for (RoleAcl roleAcl : rolesAcls) {
			if (null != roleAcl) {
				aclList.addAll(roleAcl.getAcls());
			}
		}
		
		for (Acl acl : aclList) {
			if (null != acl) {
				allAcls.add(acl.getAcl());
			}
		}
		
//		Set<String> roles = userRolesStore.getRolesForUser(userId);
//		if (roles == null) return false;
//		for (String role: roles) {
//			Set<String> roleAcls = roleAclsStore.getAclsForRole(role);
//			allAcls.addAll(roleAcls);
//		}
		return !Collections.disjoint(allAcls, Arrays.asList(acls));
	}
}
