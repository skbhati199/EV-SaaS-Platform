# WebSocket Security Configuration Guide

## Overview

This document explains how to configure Spring Security to secure API endpoints with JWT authentication while keeping WebSocket connections (specifically OCPP endpoints) unsecured.

## Implementation

The current configuration in `SecurityConfig.java` allows:
1. WebSocket endpoints (`/ocpp/**`) to be accessed without authentication
2. Heartbeat endpoints to be accessible without authentication
3. Documentation endpoints (Swagger/OpenAPI) to be accessible without authentication
4. All other REST API endpoints to require JWT authentication

## Key Components

### 1. Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                // WebSocket endpoints are not secured
                .requestMatchers("/ocpp/**").permitAll()
                // Heartbeat endpoints (station and EVSE)
                .requestMatchers("/api/v1/stations/*/heartbeat/**").permitAll()
                .requestMatchers("/api/v1/evse/heartbeat/**").permitAll()
                // Swagger/OpenAPI docs
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                // Actuator endpoints
                .requestMatchers("/actuator/**").permitAll()
                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
            
        return http.build();
    }
    
    // JWT configuration
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HMACSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKey).build();
    }
}
```

### 2. WebSocket Configuration

For WebSockets, Spring Security uses a different security context than HTTP requests. The `/ocpp/**` path is explicitly allowed without authentication in the security configuration.

### 3. Testing WebSocket Connections

To test WebSocket connections without JWT authentication, you can use tools like:

1. **Websocat** (Command-line WebSocket client):
   ```bash
   websocat ws://localhost:8082/ocpp/STATION_ID
   ```

2. **Python script with websockets library**:
   ```python
   #!/usr/bin/env python3
   
   import asyncio
   import websockets
   import json
   import uuid
   from datetime import datetime
   
   # OCPP message types
   CALL = 2
   CALLRESULT = 3
   CALLERROR = 4
   
   async def ocpp_client():
       station_id = "TEST_STATION_001"
       uri = f"ws://localhost:8082/ocpp/{station_id}"
       
       async with websockets.connect(uri) as ws:
           # Send OCPP messages, e.g., BootNotification
           message_id = str(uuid.uuid4())
           boot_request = [
               CALL,
               message_id,
               "BootNotification",
               {
                   "chargePointVendor": "Test Vendor",
                   "chargePointModel": "Test Model"
               }
           ]
           await ws.send(json.dumps(boot_request))
           response = await ws.recv()
           print(f"Received: {response}")
   
   asyncio.run(ocpp_client())
   ```

### 4. Testing API Endpoints with JWT

API endpoints can be tested using curl with a valid JWT token:

```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" http://localhost:8082/api/v1/stations
```

## Conclusion

This configuration successfully separates security requirements:
- WebSocket connections for OCPP communication don't require authentication
- API endpoints are properly secured with JWT
- Specific public endpoints like heartbeats and documentation are accessible without authentication

This approach maintains security for sensitive API operations while allowing charging stations to communicate via WebSockets without the overhead of JWT authentication. 