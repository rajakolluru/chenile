package org.chenile.configuration.security;

import org.chenile.security.KeycloakConnectionDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConnectionConfiguration {
    @Value("${chenile.security.keycloak.host}")
    String keycloakHost;

    @Value("${chenile.security.keycloak.port}")
    int keycloakPort;

    @Value("${chenile.security.keycloak.base.realm}")
    String keycloakBaseRealm;

    @Bean
    public KeycloakConnectionDetails connectionDetails(){
        KeycloakConnectionDetails conn = new KeycloakConnectionDetails();
        conn.host = keycloakHost;
        conn.httpPort = keycloakPort ;
        conn.baseRealm = keycloakBaseRealm;
        return conn;
    }
}
