package org.chenile.configuration.security;


import jakarta.servlet.http.HttpServletRequest;
import org.chenile.core.context.HeaderUtils;
import org.chenile.security.interceptor.SecurityInterceptor;
import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.util.JsonSerialization;
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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtBearerTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.security.config.oauth2.client.CommonOAuth2Provider.OKTA;

@Configuration
public class SecurityConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    String jwkSetUri;

    @Value("${chenile.security.keycloak.host}")
    String keycloakHost;

    @Value("${chenile.security.keycloak.port}")
    String keycloakPort;

    @Value("${chenile.security.keycloak.base.realm}")
    String keycloakBaseRealm;

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
                String authBaseUri = "http://" + keycloakHost + ":" + keycloakPort +
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


    private ClientRegistrationRepository clientRegistrationRepository(String tenantId) {
        InMemoryClientRegistrationRepository repo =
                new InMemoryClientRegistrationRepository(client(tenantId));
        return repo;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(
            ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    public ClientRegistration client() {
        return client(keycloakBaseRealm);
    }

    @Bean
    public ClientRegistration client(String realm) {
        ClientRegistration.Builder builder = OKTA.getBuilder("authz_servlet");
        builder.clientId("authz-servlet");
        builder.clientSecret("secret");
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
            return jwt(tenantId);
        };
    }

    private AuthenticationManager jwt(String tenantId) {
        JwtAuthenticationProvider authenticationProvider = new JwtAuthenticationProvider(jwtDecoder(tenantId));
        authenticationProvider.setJwtAuthenticationConverter(jwtBearerTokenAuthenticationConverter());
        return new ProviderManager(authenticationProvider);
    }

    private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtBearerTokenAuthenticationConverter() {
        return new JwtBearerTokenAuthenticationConverter();
    }

    private String keycloakBaseUrl(String realm) {
        return "http://" + keycloakHost + ":" + keycloakPort + "/realms/" + realm + "/";
    }

    private String keycloakOpenIdUrl(String realm) {
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


    private ServletPolicyEnforcerFilter createPolicyEnforcerFilter() {
        PolicyEnforcerConfig config;
        try {
            config = JsonSerialization.readValue(getClass().getResourceAsStream("/policy-enforcer.json"), PolicyEnforcerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ServletPolicyEnforcerFilter(new ConfigurationResolver() {
            @Override
            public PolicyEnforcerConfig resolve(HttpRequest request) {
                return config;
            }
        });
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapperForKeycloak() {
        return authorities -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            GrantedAuthority authority = authorities.iterator().next();
            boolean isOidc = authority instanceof OidcUserAuthority;

            if (isOidc) {
                OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                if (userInfo.hasClaim("roles")) {
                    Collection<String> roles = userInfo.getClaimAsStringList("roles");
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            } else {
                OAuth2UserAuthority oauth2UserAuthority = (OAuth2UserAuthority) authority;
                Map<String, Object> userAttributes = oauth2UserAuthority.getAttributes();

                if (userAttributes.containsKey("roles")) {
                    Collection<String> roles = (Collection<String>) userAttributes.get("roles");
                    mappedAuthorities.addAll(generateAuthoritiesFromClaim(roles));
                }
            }
            return mappedAuthorities;
        };
    }

    Collection<GrantedAuthority> generateAuthoritiesFromClaim(Collection<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }

    @Bean
    public SecurityInterceptor securityInterceptor() {
        return new SecurityInterceptor();
    }
}
