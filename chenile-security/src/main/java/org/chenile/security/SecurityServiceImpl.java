package org.chenile.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
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

        debug("=============== start of security context holder");
        debug("User name is " + toS(authentication.getName()) );
        debug("Principal class is " + toS(principal.getClass().getName()));

        if (principal instanceof DefaultOidcUser oidcUser) {
            auths.addAll(oidcUser.getAuthorities());
            debug("User info = " + toS(oidcUser.getUserInfo()));
            debug("claims = " + toS(oidcUser.getClaims()));
        }
        if (principal instanceof DefaultOAuth2AuthenticatedPrincipal p){
            debug("name = " + p.getName());
            for(Map.Entry<String,Object> entry: p.getAttributes().entrySet()){
                if (!entry.getKey().equals("exp"))
                    debug ("Attribute:" + entry.getKey() + "=" +  entry.getValue());
            }

        }

        debug("Principal is " + toS(principal));
        debug("authorities = " + toS(auths) );
        debug("details = " + toS(authentication.getDetails()) );
        debug("credentials = " + toS(authentication.getCredentials()) );
        debug("=============== end of security context holder");
        return auths;
    }

    private static class X {
        @JsonIgnore
        public Instant issuedAt;
        @JsonIgnore
        public Instant expiresAt;
        @JsonIgnore
        public Map<?,?> attributes;
    }

    public static Optional<Object> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }

    private static Object extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof DefaultOidcUser oUser) {
            return oUser.getUserInfo();
        } else if (authentication.getPrincipal() instanceof UserDetails springSecurityUser) {
            return springSecurityUser.getUsername();
        } else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        } else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }
        return null;
    }
    private String toS(Object object){
        if (object == null)return "null";
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(org.springframework.security.oauth2.core.OAuth2AccessToken.class,
                X.class);
        mapper.addMixIn(org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal.class,
               X.class);
        try {
            return mapper.writeValueAsString(object);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return object.toString();
        }
    }

    private void debug(String s){
        System.out.println(s);
        logger.debug(s);
    }

    @Override
    public String[] getCurrentAuthorities() {
        List<String> authorities =
        Objects.requireNonNull(getAuthorities()).stream().map(GrantedAuthority::getAuthority).toList();
        String[] s = new String[authorities.size()];
        logger.debug("Current Authorities are : {}", authorities);
        return authorities.toArray(s);
    }

    @Override
    public boolean doesCurrentUserHaveGuardingAuthorities(ChenileExchange exchange) {
        String[] guardingAuthorities = securityConfigService.getGuardingAuthorities(exchange);
        if(guardingAuthorities == null)
            return true;
        String[] currentAuthorities = getCurrentAuthorities();
        if(currentAuthorities == null)
            throw new ErrorNumException(HttpStatus.UNAUTHORIZED.value(),10000,new Object[]{});
        return guardingAuthoritiesFoundInCurrentAuthorities(guardingAuthorities,currentAuthorities);
    }

    public boolean doesCurrentUserHaveGuardingAuthorities(String...acls){
        String[] currentAuthorities = getCurrentAuthorities();
        return guardingAuthoritiesFoundInCurrentAuthorities(acls,currentAuthorities);
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
