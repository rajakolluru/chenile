package org.chenile.configuration.security;


import jakarta.servlet.http.HttpServletRequest;
import org.chenile.core.context.HeaderUtils;
import org.chenile.security.KeycloakConnectionDetails;
import org.chenile.security.SecurityServiceImpl;
import org.chenile.security.service.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ChenileSecurityConfiguration {
    private final Logger logger = LoggerFactory.getLogger(ChenileSecurityConfiguration.class);
    @Autowired
    KeycloakConnectionDetails connectionDetails;
    @Value("${chenile.security.client.id}")
    String clientId;
    @Value("${chenile.security.client.secret}")
    String clientSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(
                        (authorize) -> authorize.anyRequest().authenticated())
                .oauth2Login(auth -> {
                    // auth.clientRegistrationRepository(httpRequest-> client(httpRequest));
                    auth.authorizationEndpoint().authorizationRequestResolver(resolver());
                })
                .oauth2Client(Customizer.withDefaults())
                .oauth2ResourceServer((oauth2) ->
                        oauth2.authenticationManagerResolver(authenticationManagerResolver())
                );
        return http.build();
    }

    private OAuth2AuthorizationRequestResolver resolver() {
        return new OAuth2AuthorizationRequestResolver() {

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
                String tenantId = request.getHeader(HeaderUtils.TENANT_ID_KEY);
               String authBaseUri = "http://" + connectionDetails.host + ":" + connectionDetails.httpPort +
                //String authBaseUri = connectionDetails.host +
                        "/realms" + tenantId + "/protocol/openid-connect/auth";
                DefaultOAuth2AuthorizationRequestResolver resolver = new
                        DefaultOAuth2AuthorizationRequestResolver(clientRegistrationRepository(tenantId), authBaseUri);
                return resolver.resolve(request);
            }

            @Override
            public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
                System.out.println("Hi there at the resolve method with clientRegistrationId");
                return null;
            }
        };
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        InMemoryClientRegistrationRepository repo = new InMemoryClientRegistrationRepository(client());
        return repo;
    }

    // Create the client registration repository once per tenant. Reuse it from the second time
    private final Map<String,ClientRegistrationRepository> repositories = new HashMap<>();
    private ClientRegistrationRepository clientRegistrationRepository(String tenantId) {
        return repositories.computeIfAbsent(tenantId, (tenant) ->
                new InMemoryClientRegistrationRepository(client(tenant)));
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    public ClientRegistration client() {
        return client(connectionDetails.baseRealm);
    }

    @Bean
    public ClientRegistration client(String realm) {
        ClientRegistration.Builder builder = CommonOAuth2Provider.OKTA.getBuilder("authz_servlet");
        builder.clientId(clientId);
        builder.clientSecret(clientSecret);
        builder.issuerUri(keycloakBaseUrl(realm));
        builder.authorizationUri(keycloakOpenIdUrl(realm) + "auth");
        builder.tokenUri(keycloakOpenIdUrl(realm) + "token");
        builder.jwkSetUri(keycloakOpenIdUrl(realm) + "certs");
        builder.userInfoUri(keycloakOpenIdUrl(realm) + "userinfo");
        builder.scope("openid", "profile", "email");
        return builder.build();
    }

    private AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver() {
        return (request) -> {
            String tenantId = request.getHeader(HeaderUtils.TENANT_ID_KEY);
            return getAuthenticationManager(tenantId);
        };
    }

    private final Map<String,AuthenticationManager> authenticationManagers = new HashMap<>();
    /**
     * Here we take the tenant ID to construct a specific URL (with the realm as tenant ID) and
     * return an Authentication Manager for that realm. <br/>
     * Since the URL that is constructed depends on the validity of the tenant ID, it is possible
     * that we may get a 404 from keycloak. To take care of this, we will recast all exceptions
     * to "InvalidBearerTokenException" so that spring security will give a 401 in all those cases.
     * @param tenantId the tenant ID
     * @return the Authentication manager for the particular tenant ID
     */
    private AuthenticationManager getAuthenticationManager(String tenantId){
        return authenticationManagers.computeIfAbsent(tenantId, this::jwt);
    }

    private AuthenticationManager jwt(String tenantId) {
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder(tenantId));
        authenticationProvider.setJwtAuthenticationConverter(jwtBearerTokenAuthenticationConverter());

        return new ProviderManager(authenticationProvider){
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                try {
                    return super.authenticate(authentication);
                }catch(Throwable t){
                    if (!t.getClass().isAssignableFrom(InvalidBearerTokenException.class)){
                        logger.warn("Authentication error occurred. Recasting it to InvalidBearerTokenException.", t);
                        throw new InvalidBearerTokenException("Cannot authenticate bearer token",t);
                    }
                    throw t;
                }
            }
        };
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtBearerTokenAuthenticationConverter() {
        return new JwtBearerTokenAuthenticationConverter();
    }

    private String keycloakBaseUrl(String realm) {
        return "http://" + connectionDetails.host + ":" + connectionDetails.httpPort + "/realms/" + realm + "/";
       // return connectionDetails.host  + "/realms/" + realm;
    }

    private String keycloakOpenIdUrl(String realm) {
        //return keycloakBaseUrl(realm) + "/protocol/openid-connect/";
       return keycloakBaseUrl(realm) + "protocol/openid-connect/";
    }

    Map<String, JwtDecoder> jwtDecoderMap = new HashMap<>();

    JwtDecoder jwtDecoder(String tenantId) {
        return jwtDecoderMap.computeIfAbsent(tenantId,
                tenant -> {
                    String uri = keycloakOpenIdUrl(tenantId) + "certs";
                    return NimbusJwtDecoder.withJwkSetUri(uri).build();
                });
    }

    @Bean
    SecurityService securityService(){
        return new SecurityServiceImpl();
    }
}
