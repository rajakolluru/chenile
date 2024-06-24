package org.chenile.security.test;

import com.fasterxml.jackson.annotation.JsonProperty;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class BaseSecurityTest {
    static protected KeycloakContainer keycloak = null;

    static {
        keycloak = new KeycloakContainer();
        keycloak.withRealmImportFiles("config/realm-import.json",
                "config/realm-import-tenant1.json");
        keycloak.start();
    }

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("chenile.security.keycloak.host", PropertiesProvider::host);
        registry.add("chenile.security.keycloak.port", PropertiesProvider::port);
    }

    protected String getToken(String realm, String user, String password) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.put("grant_type", Collections.singletonList("password"));
        map.put("client_id", Collections.singletonList("authz-servlet"));
        map.put("client_secret", Collections.singletonList("secret"));
        map.put("username", Collections.singletonList(user));
        map.put("password", Collections.singletonList(password));

        String authServerUrl = "http://" + keycloak.getHost() + ":" + keycloak.getHttpPort() +
                "/realms/" + realm + "/protocol/openid-connect/token";
        var request = new HttpEntity<>(map, httpHeaders);
        KeyCloakToken token = restTemplate.postForObject(
                authServerUrl,
                request,
                KeyCloakToken.class
        );

        assert token != null;
        return token.accessToken();
    }

    static class PropertiesProvider {
        public static String host() {
            return keycloak.getHost();
        }

        public static int port() {
            return keycloak.getHttpPort();
        }
    }

    record KeyCloakToken(@JsonProperty("access_token") String accessToken) {
    }
}
