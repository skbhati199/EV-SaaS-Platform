package com.ev.schedulerservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.security.NoSuchAlgorithmException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                
                // Swagger / OpenAPI endpoints
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                
                // For testing in docker profile, allow all requests
                .requestMatchers("/api/v1/**").permitAll()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        try {
            return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
        } catch (Exception e) {
            logger.warn("Error creating JwtDecoder with URI. Falling back to dummy decoder: {}", e.getMessage());
            return createDummyJwtDecoder();
        }
    }
    
    private JwtDecoder createDummyJwtDecoder() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            
            return NimbusJwtDecoder.withPublicKey(publicKey).build();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create dummy JwtDecoder", e);
        }
    }
} 