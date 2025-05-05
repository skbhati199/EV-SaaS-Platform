package com.ev.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.factory.RetryGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for route-specific caching
 * - Defines which routes should be cached and their TTLs
 * - Sets up metadata for the GlobalCacheFilter to use
 */
@Configuration
public class RouteCacheConfig {
    
    @Value("${cache.enabled:true}")
    private boolean cacheEnabled;
    
    /**
     * Configure routes with cache metadata
     * These settings will be used by GlobalCacheFilter
     */
    @Bean
    public RouteLocator cacheableRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            // Station list API - cache for 5 minutes
            .route("station-service-list", r -> r
                .path("/api/stations")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .retry(config -> config.setRetries(3)
                                          .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.SERVICE_UNAVAILABLE))
                )
                .metadata(getCacheMetadata(300)) // 5 minutes TTL
                .uri("lb://station-service")
            )
            // Station status API - short cache to maintain freshness
            .route("station-service-status", r -> r
                .path("/api/stations/*/status")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .retry(config -> config.setRetries(2)
                                          .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.SERVICE_UNAVAILABLE))
                )
                .metadata(getCacheMetadata(30)) // 30 seconds TTL
                .uri("lb://station-service")
            )
            // User service - cache user profiles
            .route("user-service-profiles", r -> r
                .path("/api/users/*")
                .and()
                .method(HttpMethod.GET)
                .filters(f -> f
                    .retry(config -> config.setRetries(3)
                                          .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.SERVICE_UNAVAILABLE))
                )
                .metadata(getCacheMetadata(600)) // 10 minutes TTL
                .uri("lb://user-service")
            )
            // Billing service - cache billing plans
            .route("billing-service-plans", r -> r
                .path("/api/billing/plans")
                .and()
                .method(HttpMethod.GET)
                .metadata(getCacheMetadata(1800)) // 30 minutes TTL
                .uri("lb://billing-service")
            )
            // Analytics endpoints - higher TTL for heavier queries
            .route("analytics-data", r -> r
                .path("/api/analytics/**")
                .and()
                .method(HttpMethod.GET)
                .metadata(getCacheMetadata(900)) // 15 minutes TTL
                .uri("lb://analytics-service")
            )
            .build();
    }
    
    /**
     * Create cache metadata based on the TTL in seconds
     */
    private Map<String, Object> getCacheMetadata(int ttlSeconds) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("cache", cacheEnabled);
        metadata.put("cacheTtl", ttlSeconds);
        return metadata;
    }
} 