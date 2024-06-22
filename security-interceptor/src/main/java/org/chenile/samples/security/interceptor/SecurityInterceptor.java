package org.chenile.samples.security.interceptor;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.samples.security.AuthoritiesSupplier;
import org.chenile.samples.security.SecurityConfig;
import org.chenile.samples.security.errorcodes.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This interceptor uses {@link SecurityConfig} to secure this particular resource.
 * It looks at the authorities of the signed-in user and compares them with the ones that
 * are expected in the SecurityConfig and throws an exception if they are not available in the
 * current user. It also throws a 401 (UNAUTHORIZED) if there are  no security credentials
 * that exist. <br/>
 * Finally, if the SecurityConfig says that the resource is UNPROTECTED, it does not do anything
 */
public class SecurityInterceptor extends BaseChenileInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);
	@Autowired
	ApplicationContext applicationContext;
	public static final String SCOPE_PREFIX = "SCOPE_";
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		Collection<GrantedAuthority> currentAuthorities = getAuthorities();
		System.out.println("Authorities are " + getAuthorities());
		if(currentAuthorities == null)
			throw new ErrorNumException(HttpStatus.UNAUTHORIZED.value(), ErrorCodes.UNAUTHENTICATED.getSubError(),new Object[]{});
		String[] guardingAuthorities = getGuardingAuthorities(exchange);
		if(guardingAuthorities == null)
			return;
		if(guardingAuthoritiesNotFoundInCurrentAuthorities(guardingAuthorities,currentAuthorities)){
			throw new ErrorNumException(HttpStatus.FORBIDDEN.value(), ErrorCodes.FORBIDDEN.getSubError(),
					new Object[]{});
		}
	}

	private boolean guardingAuthoritiesNotFoundInCurrentAuthorities(String[] guardingAuthorities, Collection<GrantedAuthority> currentAuthorities) {
		for (GrantedAuthority ca: currentAuthorities){
			if(Arrays.stream(guardingAuthorities).anyMatch(ga -> (SCOPE_PREFIX + ga).equalsIgnoreCase(ca.getAuthority()))){
				return false;
			}
		}
		return true;
	}

	private String[] getGuardingAuthorities(ChenileExchange exchange){
		SecurityConfig config = getExtensionByAnnotation(SecurityConfig.class, exchange);
		if (config == null) return null;
		if (config.authorities().length != 0){
			return config.authorities();
		}
		if (!config.authoritiesSupplier().isEmpty()){
			AuthoritiesSupplier supplier = (AuthoritiesSupplier)applicationContext.
					getBean(config.authoritiesSupplier());
			return supplier.getAuthorities(exchange);
		}
		return null;
	}

	private Collection<GrantedAuthority> getAuthorities(){
		SecurityContext context = SecurityContextHolder.getContext();
		if (context == null){
			logger.info("Security Context is empty.");
			return null;
		}
		Authentication authentication = context.getAuthentication();
		if (authentication == null){
			logger.info("Did not find authentication in security context");
			return null;
		}
		Object principal = authentication.getPrincipal();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<GrantedAuthority> auths = new ArrayList<>(authorities);

		logger.debug("=============== start of security context holder");
		logger.debug("User name is " + authentication.getName() );
		logger.debug("Principal class is " + principal.getClass().getName());

		if (principal instanceof DefaultOidcUser oidcUser) {
			auths.addAll(oidcUser.getAuthorities());
		}

		logger.debug("Principal is " + principal);

		logger.debug("authorities = " + auths );
		logger.debug("details = " + authentication.getDetails() );
		logger.debug("credentials = " + authentication.getCredentials() );
		logger.debug("=============== end of security context holder");
		return auths;
	}

	/**
	 * This bypasses the logic only if the security config is configured to be unprotected or
	 * if the security config does not exist at all.<br/>
	 * @param exchange the exchange
	 * @return true if the SecurityConfig is configured to be UNPROTECTED or if config is missing
	 */
	@Override
	protected boolean bypassInterception(ChenileExchange exchange) {
		SecurityConfig config = getExtensionByAnnotation(SecurityConfig.class,exchange);
        return config == null || config.value() == SecurityConfig.ProtectionStatus.UNPROTECTED;
    }
}
