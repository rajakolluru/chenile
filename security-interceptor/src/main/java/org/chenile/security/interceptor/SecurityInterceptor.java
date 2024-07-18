package org.chenile.security.interceptor;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.context.HeaderUtils;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.http.Constants;
import org.chenile.security.errorcodes.ErrorCodes;
import org.chenile.security.model.SecurityConfig;
import org.chenile.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

/**
 * This interceptor uses {@link SecurityConfig} to secure this particular resource.It first looks at the
 * SecurityConfig to see if there are any guarding authorities for this service. If there are none then
 * this interceptor does nothing. <br/>
 * Next, it looks at the authorities of the signed-in user. It throws a 401 (UNAUTHORIZED) if the
 * authorities don't exist for the current user. Now, it  compares current authorities with the guarding
 * authorities for this service. It lets the user in if even one of the guarding authorities exist
 * for the current user. Else it throws a 403 (FORBIDDEN) <br/>
 * Finally, if the SecurityConfig says that the resource is UNPROTECTED, it does not do anything<br/>
 * Please see {@link SecurityConfig} for more details about the various annotation fields and how they are used
 */
public class SecurityInterceptor extends BaseChenileInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);
	@Autowired
	SecurityService securityService;

	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		if(!securityService.doesCurrentUserHaveGuardingAuthorities(exchange)){
			throw new ErrorNumException(HttpStatus.FORBIDDEN.value(), ErrorCodes.FORBIDDEN.getSubError(),
					new Object[]{});
		}
	}
	/**
	 * This bypasses the logic only if the security config is configured to be unprotected or
	 * if the security config does not exist at all.<br/>
	 * Also, we will only enforce it in the HTTP end point. We assume that all the other end points
	 * are secured <br/>
	 * @param exchange the exchange
	 * @return true if the SecurityConfig is configured to be UNPROTECTED or if config is missing
	 */
	@Override
	protected boolean bypassInterception(ChenileExchange exchange) {
		String entryPoint = exchange.getHeader(HeaderUtils.ENTRY_POINT,String.class);
		if (!entryPoint.equals(Constants.HTTP_ENTRY_POINT)) return true;
		SecurityConfig config = getExtensionByAnnotation(SecurityConfig.class,exchange);
        return config == null || config.value() == SecurityConfig.ProtectionStatus.UNPROTECTED;
    }
}
