package com.ev.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration for the API Gateway
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    /**
     * Security configuration for the API Gateway
     * - Secures all routes except those explicitly permitted
     * - Configures OAuth2 resource server with JWT authentication
     * - Enables CORS
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchanges -> exchanges
                        // Public endpoints (no authentication required)
                        .pathMatchers("/api/auth/login", "/api/auth/register", "/api/auth/refresh").permitAll()
                        .pathMatchers("/ocpi/versions", "/ocpi/2.2/credentials").permitAll()
                        
                        // Swagger/OpenAPI endpoints for API Gateway
                        .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/webjars/**").permitAll()
                        
                        // Swagger/OpenAPI endpoints for individual services
                        .pathMatchers("/api/*/v3/api-docs/**").permitAll()
                        .pathMatchers("/api/*/swagger-ui/**").permitAll()
                        .pathMatchers("/api/*/swagger-ui.html").permitAll()
                        .pathMatchers("/api/*/swagger-resources/**").permitAll()
                        .pathMatchers("/api/*/webjars/**").permitAll()
                        
                        // OCPI endpoints
                        .pathMatchers("/ocpi/**").permitAll()
                        
                        // Actuator endpoints
                        .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                        
                        // All other routes require authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
                .build();
    }

    /**
     * CORS configuration for the API Gateway
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*")); // In production, restrict to specific origins
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Authorization"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 