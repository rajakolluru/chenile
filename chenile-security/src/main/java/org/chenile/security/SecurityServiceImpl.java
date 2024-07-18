package org.chenile.security;

import org.chenile.base.exception.ErrorNumException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.security.service.SecurityConfigService;
import org.chenile.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.*;

public class SecurityServiceImpl implements SecurityService {
    Logger logger = LoggerFactory.getLogger(SecurityServiceImpl.class);
    @Autowired
    SecurityConfigService securityConfigService;
    public static final String SCOPE_PREFIX = "SCOPE_";
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

    @Override
    public String[] getCurrentAuthorities() {
        List<String> authorities =
        Objects.requireNonNull(getAuthorities()).stream().map(GrantedAuthority::getAuthority).toList();
        String[] s = new String[authorities.size()];
        return authorities.toArray(s);
    }

    @Override
    public boolean doesCurrentUserHaveGuardingAuthorities(ChenileExchange exchange) {
        String[] guardingAuthorities = securityConfigService.getGuardingAuthorities(exchange);
        if(guardingAuthorities == null)
            return true;
        String[] currentAuthorities = getCurrentAuthorities();
        logger.debug("Current Authorities are : {}", Arrays.toString(currentAuthorities));
        if(currentAuthorities == null)
            throw new ErrorNumException(HttpStatus.UNAUTHORIZED.value(),10000,new Object[]{});
        return guardingAuthoritiesFoundInCurrentAuthorities(guardingAuthorities,currentAuthorities);
    }

    private static boolean guardingAuthoritiesFoundInCurrentAuthorities(String[] guardingAuthorities, String[] currentAuthorities) {
        for (String ca: currentAuthorities){
            if(Arrays.stream(guardingAuthorities).anyMatch(ga ->
                    (SCOPE_PREFIX + ga).equalsIgnoreCase(ca))){
                return true;
            }
        }
        return false;
    }

}
