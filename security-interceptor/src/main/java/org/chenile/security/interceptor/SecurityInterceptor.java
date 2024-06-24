package org.chenile.security.interceptor;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.security.AuthoritiesSupplier;
import org.chenile.security.SecurityConfig;
import org.chenile.security.errorcodes.ErrorCodes;
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
import java.util.function.Function;

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
	ApplicationContext applicationContext;
	public static final String SCOPE_PREFIX = "SCOPE_";
	@Override
	protected void doPreProcessing(ChenileExchange exchange) {
		String[] guardingAuthorities = getGuardingAuthorities(exchange);
		if(guardingAuthorities == null)
			return;
		Collection<GrantedAuthority> currentAuthorities = getAuthorities();
		System.out.println("Authorities are : " + currentAuthorities);
		if(currentAuthorities == null)
			throw new ErrorNumException(HttpStatus.UNAUTHORIZED.value(), ErrorCodes.UNAUTHENTICATED.getSubError(),new Object[]{});

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
			Object supplier = applicationContext.getBean(config.authoritiesSupplier());
			return executeAuthoritiesSupplier(supplier,exchange);
		}
		return null;
	}

	/**
	 *
	 * @param obj The object
	 * @param exchange the exchange
	 * @return the authorities if available
	 */
	@SuppressWarnings("unchecked")
	private String[] executeAuthoritiesSupplier(Object obj, ChenileExchange exchange){
		String[] auths = null;
		if (obj instanceof AuthoritiesSupplier as){
			auths = as.getAuthorities(exchange);
		}
		if (obj instanceof Function<?,?> f1){
			Function<ChenileExchange,String[]> f2 = (Function<ChenileExchange,String[]>)f1;
			auths = f2.apply(exchange);
		}
		return auths;
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
