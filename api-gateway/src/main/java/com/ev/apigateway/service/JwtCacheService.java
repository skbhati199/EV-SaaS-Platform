package com.ev.apigateway.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for caching JWT token validation results
 * - Uses Redis to cache JWT tokens with their validation results
 * - Reduces load on authorization server by avoiding repetitive token validation
 * - Records cache hit/miss statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtCacheService {

    private final ReactiveRedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final CacheStatisticsService cacheStatisticsService;

    // Cache prefix for JWT tokens
    private static final String CACHE_PREFIX = "jwt:";
    private static final String CACHE_TYPE = "jwt-validation";

    /**
     * Gets the cached JWT validation result
     * @param token JWT token string
     * @return Optional with the JWT if found in cache, empty otherwise
     */
    public Mono<Jwt> getCachedJwt(String token) {
        String cacheKey = CACHE_PREFIX + generateKeyFromToken(token);
        
        return redisTemplate.opsForValue()
                .get(cacheKey)
                .cast(String.class)
                .flatMap(cachedValue -> {
                    try {
                        // Convert JSON string to Jwt object
                        Map<String, Object> jwtMap = objectMapper.readValue(cachedValue, Map.class);
                        return Mono.just(convertMapToJwt(jwtMap));
                    } catch (JsonProcessingException e) {
                        log.error("Error deserializing cached JWT: {}", e.getMessage());
                        return Mono.empty();
                    }
                })
                .doOnSuccess(jwt -> {
                    if (jwt != null) {
                        log.debug("JWT cache hit for token: {}", maskToken(token));
                        cacheStatisticsService.recordCacheHit(CACHE_TYPE);
                    } else {
                        log.debug("JWT cache miss for token: {}", maskToken(token));
                        cacheStatisticsService.recordCacheMiss(CACHE_TYPE);
                    }
                })
                .doOnError(e -> cacheStatisticsService.recordCacheMiss(CACHE_TYPE))
                .onErrorResume(e -> {
                    log.error("Error retrieving JWT from cache: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    /**
     * Caches a validated JWT token
     * @param token JWT token string
     * @param jwt Valid JWT object
     * @return Mono<Boolean> indicating success
     */
    public Mono<Boolean> cacheJwt(String token, Jwt jwt) {
        String cacheKey = CACHE_PREFIX + generateKeyFromToken(token);
        
        try {
            // Convert JWT to a JSON string for storage
            String jwtJson = objectMapper.writeValueAsString(convertJwtToMap(jwt));
            
            // Calculate TTL based on JWT expiration (minus 10 seconds for safety)
            Duration ttl = calculateTtl(jwt);
            
            return redisTemplate.opsForValue()
                    .set(cacheKey, jwtJson, ttl)
                    .doOnSuccess(result -> 
                        log.debug("Cached JWT with key {}, expiring in {} seconds", 
                                maskToken(token), ttl.getSeconds())
                    )
                    .onErrorResume(e -> {
                        log.error("Error caching JWT: {}", e.getMessage());
                        return Mono.just(false);
                    });
        } catch (JsonProcessingException e) {
            log.error("Error serializing JWT for caching: {}", e.getMessage());
            return Mono.just(false);
        }
    }

    /**
     * Generates a deterministic key from the JWT token
     * Avoids storing the full token as key for security reasons
     */
    private String generateKeyFromToken(String token) {
        // Use only the first 10 chars of hash for the key
        return Integer.toString(token.hashCode()).replace("-", "m");
    }

    /**
     * Converts Jwt object to a Map for serialization
     */
    private Map<String, Object> convertJwtToMap(Jwt jwt) {
        Map<String, Object> map = new ConcurrentHashMap<>();
        map.put("tokenValue", jwt.getTokenValue());
        map.put("headers", jwt.getHeaders());
        map.put("claims", jwt.getClaims());
        map.put("subject", jwt.getSubject());
        map.put("issuedAt", jwt.getIssuedAt());
        map.put("expiresAt", jwt.getExpiresAt());
        map.put("issuer", jwt.getIssuer());
        map.put("id", jwt.getId());
        return map;
    }

    /**
     * Converts a Map back to a Jwt object
     */
    private Jwt convertMapToJwt(Map<String, Object> map) {
        return Jwt.withTokenValue((String) map.get("tokenValue"))
                .headers(h -> h.putAll((Map<String, Object>) map.get("headers")))
                .claims(c -> c.putAll((Map<String, Object>) map.get("claims")))
                .build();
    }

    /**
     * Calculates TTL based on JWT expiration time
     * Returns 1 minute if expiration can't be determined
     */
    private Duration calculateTtl(Jwt jwt) {
        if (jwt.getExpiresAt() != null) {
            Duration ttl = Duration.between(java.time.Instant.now(), jwt.getExpiresAt());
            // Subtract 10 seconds for safety, minimum of 5 seconds
            long seconds = Math.max(5, ttl.getSeconds() - 10);
            return Duration.ofSeconds(seconds);
        }
        return Duration.ofMinutes(1); // Default 1 minute
    }

    /**
     * Masks token for logging purposes
     */
    private String maskToken(String token) {
        if (token == null || token.length() < 10) {
            return "***";
        }
        return token.substring(0, 5) + "..." + token.substring(token.length() - 5);
    }
}