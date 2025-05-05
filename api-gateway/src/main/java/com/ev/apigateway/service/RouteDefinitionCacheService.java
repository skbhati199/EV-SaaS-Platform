package com.ev.apigateway.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationContext;
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
@Slf4j
public class RouteDefinitionCacheService implements RouteDefinitionRepository {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ApplicationContext applicationContext;
    private final CacheStatisticsService cacheStatisticsService;
    
    private RouteDefinitionRepository originalRepository;
    
    private static final String ROUTE_CACHE_KEY = "api-gateway:routes";
    private static final Duration ROUTE_CACHE_TTL = Duration.ofHours(1);
    private static final String CACHE_TYPE = "routes";

    @Autowired
    public RouteDefinitionCacheService(
            ReactiveRedisTemplate<String, Object> redisTemplate,
            ApplicationContext applicationContext,
            CacheStatisticsService cacheStatisticsService) {
        this.redisTemplate = redisTemplate;
        this.applicationContext = applicationContext;
        this.cacheStatisticsService = cacheStatisticsService;
    }
    
    @PostConstruct
    public void init() {
        // Get the original repository after bean creation is complete
        String[] beanNames = applicationContext.getBeanNamesForType(RouteDefinitionRepository.class);
        for (String beanName : beanNames) {
            Object bean = applicationContext.getBean(beanName);
            if (bean != this && bean instanceof RouteDefinitionRepository) {
                this.originalRepository = (RouteDefinitionRepository) bean;
                log.info("Found original RouteDefinitionRepository: {}", beanName);
                break;
            }
        }
        
        if (this.originalRepository == null) {
            log.warn("No original RouteDefinitionRepository found, falling back to empty repository");
            this.originalRepository = new EmptyRouteDefinitionRepository();
        }
    }

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
        if (originalRepository == null) {
            return Mono.empty();
        }
        
        return originalRepository.save(route)
                .doOnSuccess(v -> {
                    // When a route is saved, invalidate the cache
                    invalidateCache().subscribe();
                });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        if (originalRepository == null) {
            return Mono.empty();
        }
        
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
        if (originalRepository == null) {
            return Flux.empty();
        }
        
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
                .map(result -> result > 0)
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
        if (originalRepository == null) {
            return Mono.just(false);
        }
        
        return invalidateCache()
                .then(Mono.defer(() -> 
                    originalRepository.getRouteDefinitions()
                        .collectList()
                        .flatMap(routes -> {
                            if (!routes.isEmpty()) {
                                // If routes exist, push them to the cache and set expiration
                                Object[] routeArray = routes.toArray(new Object[0]);
                                return redisTemplate.opsForList().rightPushAll(ROUTE_CACHE_KEY, routeArray)
                                        .flatMap(count -> redisTemplate.expire(ROUTE_CACHE_KEY, ROUTE_CACHE_TTL)
                                                .thenReturn(count));
                            }
                            return Mono.just(0L);
                        })
                        .map(count -> count > 0L)
                ))
                .doOnSuccess(result -> log.info("Route definition cache refreshed manually"))
                .onErrorResume(e -> {
                    log.error("Error refreshing route definition cache: {}", e.getMessage());
                    return Mono.just(false);
                });
    }
    
    /**
     * Empty implementation of RouteDefinitionRepository that returns no routes
     * Used as a fallback when no original repository is found
     */
    private static class EmptyRouteDefinitionRepository implements RouteDefinitionRepository {
        @Override
        public Flux<RouteDefinition> getRouteDefinitions() {
            return Flux.empty();
        }

        @Override
        public Mono<Void> save(Mono<RouteDefinition> route) {
            return Mono.empty();
        }

        @Override
        public Mono<Void> delete(Mono<String> routeId) {
            return Mono.empty();
        }
    }
} 