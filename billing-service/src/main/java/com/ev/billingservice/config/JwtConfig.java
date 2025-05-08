package com.ev.billingservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;

/**
 * This class previously contained JWT configuration.
 * It has been kept as a placeholder for documentation purposes.
 * JWT configuration has been removed in favor of Basic Auth.
 */
@Configuration
public class JwtConfig {
    
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        
        // Create a validator for the issuer claim
        OAuth2TokenValidator<Jwt> issuerValidator = JwtValidators.createDefaultWithIssuer(issuerUri);
        
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