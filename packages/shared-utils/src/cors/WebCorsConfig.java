package com.ev.shared.cors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Common CORS configuration for all EV SaaS Platform microservices
 * 
 * Add to your Spring Boot application by:
 * 1. Including this package in your component scan: @ComponentScan(basePackages = {"com.ev.yourservice", "com.ev.shared.cors"})
 * 2. Adding the required properties in your application.properties file:
 *    app.cors.allowed-origins=http://localhost:3000,https://*.nbevc.com
 *    app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS,PATCH
 *    app.cors.allowed-headers=Authorization,Content-Type,X-Requested-With,Accept
 *    app.cors.exposed-headers=Authorization,Content-Type
 *    app.cors.max-age=3600
 */
@Configuration
public class WebCorsConfig implements WebMvcConfigurer {
    
    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001,https://*.nbevc.com}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:Authorization,Content-Type,X-Requested-With,Accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers}")
    private String allowedHeaders;

    @Value("${app.cors.exposed-headers:Authorization,Content-Type}")
    private String exposedHeaders;

    @Value("${app.cors.max-age:3600}")
    private long maxAge;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = allowedOrigins.split(",");
        String[] methods = allowedMethods.split(",");
        String[] headers = allowedHeaders.split(",");
        String[] exposed = exposedHeaders.split(",");
        
        registry.addMapping("/**")
                .allowedOriginPatterns(origins)
                .allowedMethods(methods)
                .allowedHeaders(headers)
                .exposedHeaders(exposed)
                .allowCredentials(true)
                .maxAge(maxAge);
    }
} 