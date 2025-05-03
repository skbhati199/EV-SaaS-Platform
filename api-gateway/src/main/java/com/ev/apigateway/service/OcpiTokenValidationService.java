package com.ev.apigateway.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Service to validate OCPI tokens by calling the roaming-service
 */
@Service
@Slf4j
public class OcpiTokenValidationService {

    private final WebClient.Builder webClientBuilder;
    
    @Autowired
    public OcpiTokenValidationService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }
    
    /**
     * Validate an OCPI token with the roaming-service
     * @param token The token to validate
     * @return Mono<Boolean> indicating if the token is valid
     */
    public Mono<Boolean> validateToken(String token) {
        return webClientBuilder.build()
                .get()
                .uri("lb://roaming-service/api/internal/tokens/validate?token={token}", token)
                .retrieve()
                .onStatus(
                    HttpStatus::is4xxClientError, 
                    response -> {
                        log.error("Token validation failed with client error: {}", response.statusCode());
                        return Mono.just(new RuntimeException("Invalid token"));
                    }
                )
                .onStatus(
                    HttpStatus::is5xxServerError,
                    response -> {
                        log.error("Token validation failed with server error: {}", response.statusCode());
                        return Mono.just(new RuntimeException("Token validation service unavailable"));
                    }
                )
                .bodyToMono(TokenValidationResponse.class)
                .map(TokenValidationResponse::isValid)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(500))
                    .filter(throwable -> !(throwable instanceof RuntimeException)))
                .onErrorReturn(false);
    }
    
    /**
     * Response class for token validation
     */
    private static class TokenValidationResponse {
        private boolean valid;
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }
} 