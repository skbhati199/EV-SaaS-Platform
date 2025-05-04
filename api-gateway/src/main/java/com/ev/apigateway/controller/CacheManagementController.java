package com.ev.apigateway.controller;

import com.ev.apigateway.service.CacheStatisticsService;
import com.ev.apigateway.service.RouteDefinitionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for cache management operations
 * Provides endpoints to:
 * - View cache status and statistics
 * - Clear specific caches
 * - Refresh specific caches
 * - Monitor cache performance
 */
@RestController
@RequestMapping("/api-gateway/admin/cache")
@RequiredArgsConstructor
@Slf4j
public class CacheManagementController {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final RouteDefinitionCacheService routeDefinitionCacheService;
    private final CacheStatisticsService cacheStatisticsService;

    /**
     * Get detailed cache statistics
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> getCacheStats() {
        return cacheStatisticsService.getCacheStatistics()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Get key counts for different cache types
     */
    @GetMapping("/keys")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Long>>> getKeyCounts() {
        return cacheStatisticsService.getKeyCounts()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * Reset cache statistics counters
     */
    @PostMapping("/stats/reset")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> resetStatistics() {
        return cacheStatisticsService.resetStatistics()
                .then(Mono.just(ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Cache statistics reset successfully"
                ))));
    }

    /**
     * Clear all caches
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> clearAllCaches() {
        return redisTemplate.getConnectionFactory().getReactiveConnection()
                .serverCommands().flushAll()
                .thenReturn(ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "All caches cleared successfully"
                )))
                .onErrorReturn(ResponseEntity.status(500).body(Map.of(
                        "status", "error",
                        "message", "Failed to clear caches"
                )));
    }

    /**
     * Clear a specific cache by prefix
     */
    @DeleteMapping("/{cachePrefix}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> clearCacheByPrefix(@PathVariable String cachePrefix) {
        // We'll use SCAN to find keys with the prefix and then delete them
        String keyPattern = "api-gateway:" + cachePrefix + "*";
        
        return redisTemplate.scan(keyPattern)
                .collectList()
                .flatMap(keys -> {
                    if (keys.isEmpty()) {
                        return Mono.just(ResponseEntity.ok(Map.of(
                                "status", "success",
                                "message", "No keys found with prefix: " + cachePrefix
                        )));
                    }
                    
                    return redisTemplate.delete(keys.toArray())
                            .thenReturn(ResponseEntity.ok(Map.of(
                                    "status", "success",
                                    "message", String.format("Cleared %d keys with prefix: %s", keys.size(), cachePrefix)
                            )));
                })
                .onErrorReturn(ResponseEntity.status(500).body(Map.of(
                        "status", "error",
                        "message", "Failed to clear cache with prefix: " + cachePrefix
                )));
    }

    /**
     * Refresh routes cache
     */
    @PostMapping("/routes/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> refreshRoutesCache() {
        return routeDefinitionCacheService.refreshCache()
                .map(result -> ResponseEntity.ok(Map.of(
                        "status", result ? "success" : "error",
                        "message", result ? "Routes cache refreshed successfully" : "Failed to refresh routes cache"
                )))
                .onErrorReturn(ResponseEntity.status(500).body(Map.of(
                        "status", "error",
                        "message", "Error refreshing routes cache"
                )));
    }

    /**
     * Get cache health status
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> getCacheHealth() {
        return redisTemplate.getConnectionFactory().getReactiveConnection()
                .ping()
                .map(pong -> ResponseEntity.ok(Map.of(
                        "status", "up",
                        "redis", "connected",
                        "ping", pong
                )))
                .onErrorReturn(ResponseEntity.ok(Map.of(
                        "status", "down",
                        "redis", "disconnected",
                        "error", "Cannot connect to Redis"
                )));
    }
} 