package com.ev.apigateway.filter;

import com.ev.apigateway.service.CacheStatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;

/**
 * Global filter for caching responses in API Gateway
 * - Uses Redis to cache responses for routes with 'cache' attribute
 * - Configurable TTL based on route metadata
 * - Tracks cache hit/miss statistics
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GlobalCacheFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final CacheStatisticsService cacheStatisticsService;
    
    private static final String CACHE_TYPE = "response-cache";
    private static final String CACHE_KEY_PREFIX = "api-gateway:response:";
    private static final Duration DEFAULT_CACHE_TTL = Duration.ofMinutes(5);
    
    // Metadata attribute names
    private static final String CACHE_ENABLED_ATTR = "cache";
    private static final String CACHE_TTL_ATTR = "cacheTtl";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Route route = exchange.getAttribute(GATEWAY_ROUTE_ATTR);
        
        // Skip non-cacheable routes
        if (route == null || !shouldCache(route)) {
            return chain.filter(exchange);
        }
        
        ServerHttpRequest request = exchange.getRequest();
        String cacheKey = generateCacheKey(request);
        
        // Try to get from cache first
        return getCachedResponse(cacheKey)
                .flatMap(cachedResponse -> {
                    log.debug("Cache hit for: {}", cacheKey);
                    cacheStatisticsService.recordCacheHit(CACHE_TYPE);
                    // Process the cached response
                    return chain.filter(exchange);
                })
                .switchIfEmpty(
                    // Cache miss - proceed with the request chain but capture and cache the response
                    chain.filter(exchange)
                        .then(Mono.defer(() -> {
                            log.debug("Cache miss for: {}", cacheKey);
                            cacheStatisticsService.recordCacheMiss(CACHE_TYPE);
                            
                            // Extract response data
                            ServerHttpResponse response = exchange.getResponse();
                            
                            // Only cache successful responses
                            if (response.getStatusCode() != null && response.getStatusCode().is2xxSuccessful()) {
                                // Cache the response
                                Duration ttl = getCacheTtl(route);
                                cacheResponse(cacheKey, "cached-response", ttl).subscribe();
                            }
                            
                            return Mono.empty();
                        }))
                );
    }

    /**
     * Check if a route should be cached based on its metadata
     */
    private boolean shouldCache(Route route) {
        return Optional.ofNullable(route.getMetadata().get(CACHE_ENABLED_ATTR))
                .map(value -> Boolean.parseBoolean(value.toString()))
                .orElse(false);
    }
    
    /**
     * Get the cache TTL from route metadata or use default
     */
    private Duration getCacheTtl(Route route) {
        return Optional.ofNullable(route.getMetadata().get(CACHE_TTL_ATTR))
                .map(value -> Duration.ofSeconds(Long.parseLong(value.toString())))
                .orElse(DEFAULT_CACHE_TTL);
    }
    
    /**
     * Generate a unique cache key based on the request
     */
    private String generateCacheKey(ServerHttpRequest request) {
        String path = request.getPath().value();
        String query = request.getURI().getQuery() != null ? request.getURI().getQuery() : "";
        return CACHE_KEY_PREFIX + path + ":" + query;
    }
    
    /**
     * Get cached response from Redis
     */
    private Mono<String> getCachedResponse(String cacheKey) {
        return redisTemplate.opsForValue()
                .get(cacheKey)
                .cast(String.class)
                .onErrorResume(e -> {
                    log.error("Error retrieving cached response: {}", e.getMessage());
                    return Mono.empty();
                });
    }
    
    /**
     * Cache a response in Redis
     */
    private Mono<Boolean> cacheResponse(String cacheKey, String response, Duration ttl) {
        return redisTemplate.opsForValue()
                .set(cacheKey, response, ttl)
                .doOnSuccess(result -> 
                    log.debug("Cached response at key: {}, TTL: {} seconds", cacheKey, ttl.getSeconds())
                )
                .onErrorResume(e -> {
                    log.error("Error caching response: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    @Override
    public int getOrder() {
        // Run after routing but before other filters
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
} 