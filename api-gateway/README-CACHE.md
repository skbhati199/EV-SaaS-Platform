# Redis Caching in API Gateway

## Overview
The API Gateway implements a comprehensive Redis-based caching system to improve performance and reduce load on backend services. This document describes the caching architecture, configuration options, and management endpoints.

## Cache Types

### 1. JWT Token Cache
- Caches validated JWT tokens to reduce load on authentication services
- TTL is automatically calculated based on token expiration time
- Improves API gateway throughput for authenticated requests
- See `JwtCacheService.java` for implementation details

### 2. Route Definition Cache
- Caches API route definitions to speed up routing decisions
- Automatically invalidates when routes are added, modified, or deleted
- Default TTL of 1 hour
- See `RouteDefinitionCacheService.java` for implementation details

### 3. Response Cache
- Caches HTTP responses for specific routes
- Configurable on a per-route basis with different TTLs
- Only caches successful (2xx) responses
- See `GlobalCacheFilter.java` for implementation

## Configuration

### Redis Connection Configuration
Redis connection parameters are configurable in `application.yml`:

```yaml
spring:
  redis:
    host: redis
    port: 6379
    password: your_password  # Optional
    ssl: false               # Optional
```

### Cache TTL Configuration
Cache expiration times are configurable in `RedisConfig.java`:

```java
// JWT tokens - short TTL
cacheConfigurations.put("jwt-validation", defaultConfig.entryTtl(Duration.ofMinutes(1)));

// Route definitions - longer TTL
cacheConfigurations.put("routes", defaultConfig.entryTtl(Duration.ofHours(1)));

// Other cache configurations...
```

### Route-specific Cache Configuration
Routes can be configured for caching in `RouteCacheConfig.java`, with properties:
- `cache`: boolean to enable/disable caching for the route
- `cacheTtl`: TTL in seconds for cached responses

Example:
```java
.route("station-service-list", r -> r
    .path("/api/stations")
    .and()
    .method(HttpMethod.GET)
    .metadata(getCacheMetadata(300)) // 5 minutes TTL
    .uri("lb://station-service")
)
```

## Management Endpoints

The API Gateway provides cache management endpoints at `/api-gateway/admin/cache`:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api-gateway/admin/cache` | GET | Get detailed cache statistics |
| `/api-gateway/admin/cache/keys` | GET | Get key counts by cache type |
| `/api-gateway/admin/cache/stats/reset` | POST | Reset cache statistics counters |
| `/api-gateway/admin/cache` | DELETE | Clear all caches |
| `/api-gateway/admin/cache/{prefix}` | DELETE | Clear cache by prefix |
| `/api-gateway/admin/cache/routes/refresh` | POST | Manually refresh routes cache |
| `/api-gateway/admin/cache/health` | GET | Check Redis health status |

These endpoints are secured and require the `ADMIN` role to access (except health endpoint).

## Monitoring

Cache statistics are logged every 10 minutes, including:
- Memory usage
- Hit/miss ratio
- Key counts by cache type

For custom dashboard integration, the statistics can be accessed via the REST endpoints above.

## Best Practices

1. **Be selective with caching**: Not all endpoints benefit from caching. Prioritize:
   - Frequently accessed data that doesn't change often
   - Expensive operations (complex database queries, aggregations)
   - Non-personalized responses

2. **Set appropriate TTLs**:
   - Short TTL (seconds): Rapidly changing data (e.g., station status)
   - Medium TTL (minutes): Semi-stable data (e.g., user profiles)
   - Long TTL (hours): Stable reference data (e.g., tariff plans)

3. **Monitor cache effectiveness**:
   - Watch hit/miss ratio to gauge effectiveness
   - High miss rate may indicate poor cache configuration
   - Memory usage should be monitored to prevent cache eviction

4. **Cache invalidation**:
   - Use cache refresh endpoints when data is known to change
   - Consider implementing event-based cache invalidation for critical data 