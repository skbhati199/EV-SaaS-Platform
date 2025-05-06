package com.ev.billingservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

@Configuration
public class JwtConfig {
    
    // Hardcoded URLs to ensure they're used correctly
    private static final String KEYCLOAK_JWK_SET_URI = "http://keycloak:8080/auth/realms/ev-platform/protocol/openid-connect/certs";
    private static final String KEYCLOAK_ISSUER_URI = "http://localhost:8080/auth/realms/ev-platform";
    
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(KEYCLOAK_JWK_SET_URI).build();
        
        // Create a validator for the issuer claim
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(KEYCLOAK_ISSUER_URI);
        
        // Create a validator for the audience claim (allowing any audience)
        OAuth2TokenValidator<Jwt> audienceValidator = new JwtClaimValidator<>("aud", audience -> true);
        
        // Combine validators
        OAuth2TokenValidator<Jwt> combinedValidator = new DelegatingOAuth2TokenValidator<>(
                issuerValidator, audienceValidator);
        
        // Set the validators on the decoder
        jwtDecoder.setJwtValidator(combinedValidator);
        
        return jwtDecoder;
    }
} 