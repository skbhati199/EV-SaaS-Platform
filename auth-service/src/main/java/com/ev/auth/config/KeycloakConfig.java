package com.ev.auth.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    @Bean
    public Keycloak keycloak() {
        // Handle both URL formats (with /auth and without /auth for newer Keycloak versions)
        String serverUrlNormalized = serverUrl;
        if (!serverUrl.endsWith("/auth")) {
            // Check if it's already ending with a slash
            if (!serverUrl.endsWith("/")) {
                serverUrlNormalized = serverUrl + "/";
            }
        }
        
        return KeycloakBuilder.builder()
                .serverUrl(serverUrlNormalized)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType("client_credentials")
                .build();
    }
}
