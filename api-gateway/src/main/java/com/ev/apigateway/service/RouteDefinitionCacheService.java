package com.ev.apigateway.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Service for caching route definitions in API Gateway
 * - Implements RouteDefinitionRepository for Spring Cloud Gateway
 * - Stores route definitions in Redis cache
 * - Provides fallback to original repository if cache fails
 * - Records cache hit/miss statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RouteDefinitionCacheService implements RouteDefinitionRepository {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final RouteDefinitionRepository originalRepository;
    private final CacheStatisticsService cacheStatisticsService;
    
    private static final String ROUTE_CACHE_KEY = "api-gateway:routes";
    private static final Duration ROUTE_CACHE_TTL = Duration.ofHours(1);
    private static final String CACHE_TYPE = "routes";

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        // Try to get route definitions from cache first
        return redisTemplate.opsForList().range(ROUTE_CACHE_KEY, 0, -1)
                .cast(RouteDefinition.class)
                .collectList()
                .flatMapMany(cachedRoutes -> {
                    if (cachedRoutes.isEmpty()) {
                        log.debug("Route definitions cache miss, fetching from repository");
                        cacheStatisticsService.recordCacheMiss(CACHE_TYPE);
                        return fetchAndCacheRoutes();
                    } else {
                        log.debug("Found {} route definitions in cache", cachedRoutes.size());
                        cacheStatisticsService.recordCacheHit(CACHE_TYPE);
                        return Flux.fromIterable(cachedRoutes);
                    }
                })
                .onErrorResume(e -> {
                    log.error("Error retrieving route definitions from cache: {}", e.getMessage());
                    cacheStatisticsService.recordCacheMiss(CACHE_TYPE);
                    return fetchAndCacheRoutes();
                });
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        return originalRepository.save(route)
                .doOnSuccess(v -> {
                    // When a route is saved, invalidate the cache
                    invalidateCache().subscribe();
                });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        return originalRepository.delete(routeId)
                .doOnSuccess(v -> {
                    // When a route is deleted, invalidate the cache
                    invalidateCache().subscribe();
                });
    }

    /**
     * Fetch routes from original repository and cache them
     */
    private Flux<RouteDefinition> fetchAndCacheRoutes() {
        return originalRepository.getRouteDefinitions()
                .collectList()
                .doOnNext(routes -> {
                    // Clear existing cache and add all routes
                    redisTemplate.delete(ROUTE_CACHE_KEY)
                            .then(Mono.defer(() -> {
                                if (!routes.isEmpty()) {
                                    return redisTemplate.opsForList().rightPushAll(ROUTE_CACHE_KEY, routes.toArray())
                                            .then(redisTemplate.expire(ROUTE_CACHE_KEY, ROUTE_CACHE_TTL));
                                }
                                return Mono.empty();
                            }))
                            .doOnSuccess(result -> log.debug("Cached {} route definitions", routes.size()))
                            .onErrorResume(e -> {
                                log.error("Error caching route definitions: {}", e.getMessage());
                                return Mono.empty();
                            })
                            .subscribe();
                })
                .flatMapMany(Flux::fromIterable);
    }

    /**
     * Invalidate the route cache
     */
    private Mono<Boolean> invalidateCache() {
        return redisTemplate.delete(ROUTE_CACHE_KEY)
                .doOnSuccess(result -> log.debug("Route definition cache invalidated"))
                .onErrorResume(e -> {
                    log.error("Error invalidating route definition cache: {}", e.getMessage());
                    return Mono.just(false);
                });
    }

    /**
     * Manually refresh the route cache
     */
    public Mono<Boolean> refreshCache() {
        return invalidateCache()
                .then(Mono.defer(() -> 
                    originalRepository.getRouteDefinitions()
                        .collectList()
                        .flatMap(routes -> {
                            if (!routes.isEmpty()) {
                                return redisTemplate.opsForList().rightPushAll(ROUTE_CACHE_KEY, routes.toArray())
                                        .then(redisTemplate.expire(ROUTE_CACHE_KEY, ROUTE_CACHE_TTL));
                            }
                            return Mono.just(0L);
                        })
                        .map(count -> count > 0)
                ))
                .doOnSuccess(result -> log.info("Route definition cache refreshed manually"))
                .onErrorResume(e -> {
                    log.error("Error refreshing route definition cache: {}", e.getMessage());
                    return Mono.just(false);
                });
    }
} 