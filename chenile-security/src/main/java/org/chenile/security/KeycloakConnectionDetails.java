package org.chenile.security;

/**
 * Used to connect to Keycloak. This class needs to be updated if the conn parameters change.
 * cucumber-sec-utils updates it. It is initialized from property files on startup in security-interceptor
 */
public class KeycloakConnectionDetails {
    public String host;
    public int httpPort;
    public String baseRealm;
}
