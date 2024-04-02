package org.chenile.security.stmutils;
import org.chenile.core.context.ContextContainer;
import org.chenile.security.service.SecurityService;
import org.chenile.stm.STMSecurityStrategy;
import org.springframework.beans.factory.annotation.Autowired;


public class SecurityStrategy implements STMSecurityStrategy {
	@Autowired private ContextContainer contextContainer;
	@Autowired private SecurityService securityService;
	/**
	 * @param securityService the securityService to set
	 */
	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}
	/**
	 * @param contextContainer the contextContainer to set
	 */
	public void setContextContainer(ContextContainer contextContainer) {
		this.contextContainer = contextContainer;
	}
	@Override
	public boolean isAllowed(String... acls) {
		return securityService.isAllowed(contextContainer.getUser(), acls);
	}

}
