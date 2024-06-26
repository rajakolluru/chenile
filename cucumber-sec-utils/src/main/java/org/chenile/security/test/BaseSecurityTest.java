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

/**
 * A security test container initiating class. Initiates a keycloak container to test security roles.
 * This class can be used in multiple ways:<br/>
 * <ul>
 * <li>As a super class for your test case.</li>
 * <li>As a utility class that can be called.</li>
 * <li>Packaged within Cucumber. See {@link org.chenile.cucumber.security.rest.RestCukesSecSteps}</li>
 * </ul>
 * Its main purpose is to generate authorization tokens that can be injected into requests to test
 * secure services. Without these tokens, the requests will be rejected by the Chenile security
 * interceptor. <br/>
 * This keycloak test container will have the following realms / users/ roles /scopes:<br/>
 * <table class="striped">
 *     <tr>
 *         <th>Realm</th><th>User/Password</th><th>Role(s)</th><th>Scope(s)</th>
 *     </tr>
 *     <tr>
 *         <td>tenant0</td><td>t0-normal/t0-normal</td><td>user</td><td>test.normal</td>
 *     </tr>
 *     <tr>
 *          <td>tenant0</td><td>t0-premium/t0-premium</td><td>user,user_premium</td><td>test.normal,test.premium</td>
 *     </tr>
 *     <tr>
 *          <td>tenant1</td><td>t1-normal/t1-normal</td><td>user</td><td>test.normal</td>
 *     </tr>
 *    <tr>
 *         <td>tenant1</td><td>t1-premium/t1-premium</td><td>user,user_premium</td><td>test.normal,test.premium</td>
 *    </tr>
 * </table>
 */
public class BaseSecurityTest {

    public static KeycloakContainer keycloak =
         new KeycloakContainer()
                .withRealmImportFiles("config/realm-import-tenant0.json",
                "config/realm-import-tenant1.json")
                ;
    static{
        keycloak.start();
    }

    public static String getHost(){
        return keycloak.getHost();
    }

    public static int getHttpPort(){
        return keycloak.getHttpPort();
    }


    @DynamicPropertySource
    public static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("chenile.security.keycloak.host", keycloak::getHost);
        registry.add("chenile.security.keycloak.port", keycloak::getHttpPort);
    }

    public static String getToken(String realm, String user, String password) {
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

    record KeyCloakToken(@JsonProperty("access_token") String accessToken) {
    }
}
