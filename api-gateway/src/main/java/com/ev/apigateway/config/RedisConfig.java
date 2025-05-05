package com.ev.apigateway.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import io.lettuce.core.ClientOptions;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis Configuration for API Gateway
 * - Sets up reactive Redis connection factory
 * - Configures Redis templates for caching
 * - Sets up cache TTLs for different cache types
 */
@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    @Value("${spring.redis.host:redis}")
    private String redisHost;

    @Value("${spring.redis.port:6379}")
    private int redisPort;

    @Value("${spring.redis.password:}")
    private String redisPassword;

    @Value("${spring.redis.ssl:false}")
    private boolean redisSsl;

    /**
     * Creates a reactive Redis connection factory
     */
    @Bean
    @Primary
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        
        if (redisPassword != null && !redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }

        ClientOptions clientOptions = ClientOptions.builder()
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build();

        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder()
                .clientOptions(clientOptions);
                
        // Apply SSL configuration if enabled
        if (redisSsl) {
            builder.useSsl();
        }
        
        LettuceClientConfiguration clientConfig = builder.build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisConfig, clientConfig);
        log.info("Configured Redis connection to {}:{}", redisHost, redisPort);
        return factory;
    }

    /**
     * Creates a reactive Redis template for string keys and JSON values
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        
        RedisSerializationContext<String, Object> context = builder
                .value(serializer)
                .build();
        
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);
    }

    /**
     * Creates a standard Redis connection factory for the cache manager
     */
    @Bean("standardRedisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        
        if (redisPassword != null && !redisPassword.isEmpty()) {
            redisConfig.setPassword(redisPassword);
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
        
        // Apply SSL configuration if enabled
        if (redisSsl) {
            builder.useSsl();
        }
        
        LettuceClientConfiguration clientConfig = builder.build();

        return new LettuceConnectionFactory(redisConfig, clientConfig);
    }

    /**
     * Configure Redis Cache Manager with appropriate TTL values for different cache types
     */
    @Bean
    public RedisCacheManager redisCacheManager(@Qualifier("standardRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        // Default cache configuration with TTL of 5 minutes
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .prefixCacheNameWith("api-gateway:");
        
        // Configure different TTLs for different cache types
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Route definitions - longer TTL since they don't change often
        cacheConfigurations.put("routes", defaultConfig.entryTtl(Duration.ofHours(1)));
        
        // User permissions cache - medium TTL
        cacheConfigurations.put("user-permissions", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        
        // JWT token validation results - short TTL
        cacheConfigurations.put("jwt-validation", defaultConfig.entryTtl(Duration.ofMinutes(1)));
        
        // Station status cache - very short TTL
        cacheConfigurations.put("station-status", defaultConfig.entryTtl(Duration.ofSeconds(30)));
        
        // Metrics data cache - medium TTL
        cacheConfigurations.put("metrics", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        
        // Service registry cache - medium TTL
        cacheConfigurations.put("service-registry", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        
        log.info("Configured Redis cache manager with {} cache configurations", cacheConfigurations.size());
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
} 