package com.ev.apigateway.filter;

import com.ev.apigateway.service.OcpiTokenValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Custom Gateway Filter for OCPI Authorization
 * This filter validates the OCPI token in the Authorization header
 */
@Component
@Slf4j
public class OcpiAuthorizationFilter extends AbstractGatewayFilterFactory<OcpiAuthorizationFilter.Config> {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Token ";
    
    private final OcpiTokenValidationService tokenValidationService;
    
    @Autowired
    public OcpiAuthorizationFilter(OcpiTokenValidationService tokenValidationService) {
        super(Config.class);
        this.tokenValidationService = tokenValidationService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            
            // Skip token validation for version and credentials endpoints
            String path = request.getURI().getPath();
            if (isPublicEndpoint(path)) {
                return chain.filter(exchange);
            }
            
            // Check if Authorization header exists and has the correct format
            if (!request.getHeaders().containsKey(AUTHORIZATION_HEADER)) {
                log.error("Missing Authorization header for OCPI request");
                return handleUnauthorized(exchange, "Missing Authorization header");
            }
            
            String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
            if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
                log.error("Invalid Authorization header format for OCPI request");
                return handleUnauthorized(exchange, "Invalid Authorization header format");
            }
            
            String token = authHeader.substring(TOKEN_PREFIX.length());
            
            log.info("Validating OCPI token: {}", token);
            
            return tokenValidationService.validateToken(token)
                .flatMap(isValid -> {
                    if (isValid) {
                        log.info("OCPI token validation successful");
                        return chain.filter(exchange);
                    } else {
                        log.error("OCPI token validation failed");
                        return handleUnauthorized(exchange, "Invalid token");
                    }
                });
        };
    }
    
    private boolean isPublicEndpoint(String path) {
        return path.equals("/ocpi/versions") || 
               path.equals("/ocpi/2.2/credentials") ||
               path.endsWith("/2.2");
    }
    
    private Mono<Void> handleUnauthorized(ServerWebExchange exchange, String errorMessage) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
    
    public static class Config {
        // Configuration properties can be added here if needed
    }
} 