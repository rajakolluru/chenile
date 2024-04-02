package org.chenile.configuration.security.service;

import org.chenile.security.service.SecurityService;
import org.chenile.security.service.impl.RoleAclsStore;
import org.chenile.security.service.impl.RoleAclsStoreImpl;
import org.chenile.security.service.impl.SecurityServiceImpl;
import org.chenile.security.service.impl.UserRolesStore;
import org.chenile.security.service.impl.UserRolesStoreImpl;
import org.chenile.security.stmutils.SecurityStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfiguration {

	@Bean public SecurityStrategy securityStrategy() {
		return new SecurityStrategy();
	}
	
	@Bean public SecurityService securityService() {
		return new SecurityServiceImpl();
	}
	
	@Bean public RoleAclsStore roleAclsStore() {
		return new RoleAclsStoreImpl();
	}
	
	@Bean public UserRolesStore userRolesStore() {
		return new UserRolesStoreImpl();
	}
	
}
