package com.ev.apigateway.controller;

import com.ev.apigateway.service.CacheStatisticsService;
import com.ev.apigateway.service.RouteDefinitionCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", "success");
        responseMap.put("message", "Cache statistics reset successfully");
        
        return cacheStatisticsService.resetStatistics()
                .then(Mono.just(ResponseEntity.ok(responseMap)));
    }

    /**
     * Clear all caches
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> clearAllCaches() {
        Map<String, Object> successMap = new HashMap<>();
        successMap.put("status", "success");
        successMap.put("message", "All caches cleared successfully");
        
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("status", "error");
        errorMap.put("message", "Failed to clear caches");
        
        return redisTemplate.getConnectionFactory().getReactiveConnection()
                .serverCommands().flushAll()
                .thenReturn(ResponseEntity.ok(successMap))
                .onErrorReturn(ResponseEntity.status(500).body(errorMap));
    }

    /**
     * Clear a specific cache by prefix
     */
    @DeleteMapping("/{cachePrefix}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> clearCacheByPrefix(@PathVariable String cachePrefix) {
        // We'll use SCAN to find keys with the prefix and then delete them
        String keyPattern = "api-gateway:" + cachePrefix + "*";
        ScanOptions options = ScanOptions.scanOptions().match(keyPattern).build();
        
        Map<String, Object> emptyMap = new HashMap<>();
        emptyMap.put("status", "success");
        emptyMap.put("message", "No keys found with prefix: " + cachePrefix);
        
        return redisTemplate.scan(options)
                .collectList()
                .flatMap(keys -> {
                    if (keys.isEmpty()) {
                        return Mono.just(ResponseEntity.ok(emptyMap));
                    }
                    
                    Map<String, Object> successMap = new HashMap<>();
                    successMap.put("status", "success");
                    successMap.put("message", String.format("Cleared %d keys with prefix: %s", keys.size(), cachePrefix));
                    
                    return Flux.fromIterable(keys)
                            .flatMap(key -> redisTemplate.delete((String)key))
                            .reduce(0L, (count, deleted) -> count + deleted)
                            .map(totalDeleted -> ResponseEntity.ok(successMap));
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("status", "error");
                    errorMap.put("message", "Failed to clear cache with prefix: " + cachePrefix);
                    return Mono.just(ResponseEntity.status(500).body(errorMap));
                });
    }

    /**
     * Refresh routes cache
     */
    @PostMapping("/routes/refresh")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<ResponseEntity<Map<String, Object>>> refreshRoutesCache() {
        return routeDefinitionCacheService.refreshCache()
                .map(result -> {
                    Map<String, Object> responseMap = new HashMap<>();
                    responseMap.put("status", result ? "success" : "error");
                    responseMap.put("message", result ? "Routes cache refreshed successfully" : "Failed to refresh routes cache");
                    return ResponseEntity.ok(responseMap);
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("status", "error");
                    errorMap.put("message", "Error refreshing routes cache");
                    return Mono.just(ResponseEntity.status(500).body(errorMap));
                });
    }

    /**
     * Get cache health status
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> getCacheHealth() {
        return redisTemplate.getConnectionFactory().getReactiveConnection()
                .ping()
                .map(pong -> {
                    Map<String, Object> successMap = new HashMap<>();
                    successMap.put("status", "up");
                    successMap.put("redis", "connected");
                    successMap.put("ping", pong);
                    return ResponseEntity.ok(successMap);
                })
                .onErrorResume(e -> {
                    Map<String, Object> errorMap = new HashMap<>();
                    errorMap.put("status", "down");
                    errorMap.put("redis", "disconnected");
                    errorMap.put("error", "Cannot connect to Redis");
                    return Mono.just(ResponseEntity.ok(errorMap));
                });
    }
} 