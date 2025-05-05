package com.ev.apigateway.filter;

import com.ev.apigateway.service.JwtCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

/**
 * Filter for caching JWT token authentication
 * - Intercepts requests to extract JWT tokens
 * - Checks Redis cache for pre-validated tokens
 * - Falls back to normal JWT validation if not in cache
 * - Stores validated tokens in cache
 * 
 * NOTE: Currently disabled until proper ReactiveAuthenticationManager is configured
 */
// @Component - Temporarily disabled until ReactiveAuthenticationManager is properly configured
@RequiredArgsConstructor
@Slf4j
public class CachingJwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtDecoder jwtDecoder;
    private final JwtCacheService jwtCacheService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Skip if no Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);
        
        return authenticateWithCache(token)
                .switchIfEmpty(authenticateWithProvider(token))
                .flatMap(authentication -> 
                    chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                );
    }

    /**
     * Try to authenticate using cached JWT token
     */
    private Mono<Authentication> authenticateWithCache(String token) {
        return jwtCacheService.getCachedJwt(token)
                .flatMap(jwt -> {
                    log.debug("Using cached JWT token");
                    BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(token);
                    return authenticationManager.authenticate(authToken);
                });
    }

    /**
     * Authenticate with the authentication manager and cache the result
     */
    private Mono<Authentication> authenticateWithProvider(String token) {
        log.debug("JWT token not in cache, authenticating with provider");
        BearerTokenAuthenticationToken authToken = new BearerTokenAuthenticationToken(token);
        
        return authenticationManager.authenticate(authToken)
                .flatMap(authentication -> {
                    // Extract JWT from the authentication
                    if (authentication.getCredentials() instanceof Jwt jwt) {
                        // Cache the successful JWT validation
                        return jwtCacheService.cacheJwt(token, jwt)
                                .thenReturn(authentication);
                    }
                    return Mono.just(authentication);
                });
    }

    @Override
    public int getOrder() {
        // Run after security filter but before route filters
        return -1;
    }
} 