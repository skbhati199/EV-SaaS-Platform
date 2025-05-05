package com.ev.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service for collecting and reporting Redis cache statistics
 * - Tracks cache hits and misses
 * - Reports memory usage
 * - Provides insights into cache performance
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CacheStatisticsService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    
    // Cache metrics counters
    private final Map<String, AtomicLong> cacheHits = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> cacheMisses = new ConcurrentHashMap<>();
    
    // Cache types
    private static final String[] CACHE_TYPES = {
        "jwt-validation", 
        "routes", 
        "user-permissions", 
        "station-status", 
        "metrics", 
        "service-registry"
    };
    
    /**
     * Record a cache hit for a specific cache type
     */
    public void recordCacheHit(String cacheType) {
        cacheHits.computeIfAbsent(cacheType, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Record a cache miss for a specific cache type
     */
    public void recordCacheMiss(String cacheType) {
        cacheMisses.computeIfAbsent(cacheType, k -> new AtomicLong(0)).incrementAndGet();
    }
    
    /**
     * Get detailed cache statistics for all cache types
     */
    public Mono<Map<String, Object>> getCacheStatistics() {
        return getRedisInfo()
            .map(redisInfo -> {
                Map<String, Object> stats = new HashMap<>();
                
                // Add Redis server info
                stats.put("redisInfo", redisInfo);
                
                // Add hit/miss statistics for all cache types
                Map<String, Object> hitMissStats = new HashMap<>();
                for (String cacheType : CACHE_TYPES) {
                    Map<String, Object> typeStat = new HashMap<>();
                    long hits = cacheHits.getOrDefault(cacheType, new AtomicLong(0)).get();
                    long misses = cacheMisses.getOrDefault(cacheType, new AtomicLong(0)).get();
                    long total = hits + misses;
                    
                    typeStat.put("hits", hits);
                    typeStat.put("misses", misses);
                    typeStat.put("total", total);
                    typeStat.put("hitRatio", total > 0 ? (double) hits / total : 0);
                    
                    hitMissStats.put(cacheType, typeStat);
                }
                stats.put("cacheStats", hitMissStats);
                
                return stats;
            });
    }
    
    /**
     * Get Redis server information including memory usage
     */
    private Mono<Map<String, Object>> getRedisInfo() {
        return redisTemplate.getConnectionFactory().getReactiveConnection()
            .serverCommands().info()
            .map(info -> {
                Map<String, Object> redisInfo = new HashMap<>();
                
                // Extract memory information
                String infoStr = info.toString();
                String[] lines = infoStr.split("\r\n");
                for (String line : lines) {
                    if (line.startsWith("used_memory:")) {
                        redisInfo.put("usedMemory", Long.parseLong(line.split(":")[1]));
                    } else if (line.startsWith("used_memory_human:")) {
                        redisInfo.put("usedMemoryHuman", line.split(":")[1]);
                    } else if (line.startsWith("used_memory_peak:")) {
                        redisInfo.put("peakMemory", Long.parseLong(line.split(":")[1]));
                    } else if (line.startsWith("used_memory_peak_human:")) {
                        redisInfo.put("peakMemoryHuman", line.split(":")[1]);
                    } else if (line.startsWith("total_connections_received:")) {
                        redisInfo.put("totalConnections", Long.parseLong(line.split(":")[1]));
                    } else if (line.startsWith("keyspace_hits:")) {
                        redisInfo.put("keyspaceHits", Long.parseLong(line.split(":")[1]));
                    } else if (line.startsWith("keyspace_misses:")) {
                        redisInfo.put("keyspaceMisses", Long.parseLong(line.split(":")[1]));
                    }
                }
                
                // Calculate hit ratio from Redis keyspace stats
                long keyspaceHits = (long) redisInfo.getOrDefault("keyspaceHits", 0L);
                long keyspaceMisses = (long) redisInfo.getOrDefault("keyspaceMisses", 0L);
                long totalKeyspace = keyspaceHits + keyspaceMisses;
                redisInfo.put("globalHitRatio", totalKeyspace > 0 ? (double) keyspaceHits / totalKeyspace : 0);
                
                return redisInfo;
            })
            .onErrorResume(e -> {
                log.error("Error getting Redis info: {}", e.getMessage());
                Map<String, Object> fallback = new HashMap<>();
                fallback.put("status", "error");
                fallback.put("message", "Cannot retrieve Redis info: " + e.getMessage());
                return Mono.just(fallback);
            });
    }
    
    /**
     * Get the number of keys by pattern to analyze cache size
     */
    public Mono<Map<String, Long>> getKeyCounts() {
        Map<String, Long> counts = new HashMap<>();
        
        return Mono.just(counts)
            .flatMap(map -> {
                // Build a Mono chain to count keys for each cache type
                Mono<Map<String, Long>> result = Mono.just(map);
                
                for (String cacheType : CACHE_TYPES) {
                    String pattern = "api-gateway:" + cacheType + "*";
                    result = result.flatMap(m -> 
                        countKeys(pattern)
                            .doOnNext(count -> m.put(cacheType, count))
                            .thenReturn(m)
                    );
                }
                
                return result;
            });
    }
    
    /**
     * Count keys matching a pattern
     */
    private Mono<Long> countKeys(String pattern) {
        ScanOptions options = ScanOptions.scanOptions().match(pattern).build();
        return redisTemplate.scan(options)
            .count()
            .onErrorReturn(0L);
    }
    
    /**
     * Log cache statistics periodically
     */
    @Scheduled(fixedRate = 600000) // Every 10 minutes
    public void logCacheStatistics() {
        getCacheStatistics()
            .flatMap(stats -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> redisInfo = (Map<String, Object>) stats.get("redisInfo");
                
                log.info("Redis Cache Statistics:");
                log.info("Memory usage: {}", redisInfo.get("usedMemoryHuman"));
                log.info("Global hit ratio: {}", redisInfo.get("globalHitRatio"));
                
                return getKeyCounts();
            })
            .subscribe(keyCounts -> 
                log.info("Cache key counts: {}", keyCounts)
            );
    }
    
    /**
     * Reset all cache statistics counters
     */
    public Mono<Void> resetStatistics() {
        return Mono.fromRunnable(() -> {
            cacheHits.clear();
            cacheMisses.clear();
            log.info("Cache statistics reset");
        });
    }
} 