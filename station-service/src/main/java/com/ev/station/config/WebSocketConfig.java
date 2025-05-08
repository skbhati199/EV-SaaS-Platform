package com.ev.station.config;

import com.ev.station.ocpp.OcppWebSocketHandler;
import com.ev.station.ocpp.StationHandshakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer, WebSocketMessageBrokerConfigurer {

    private final OcppWebSocketHandler ocppWebSocketHandler;
    private final StationHandshakeInterceptor handshakeInterceptor;

    public WebSocketConfig(OcppWebSocketHandler ocppWebSocketHandler, StationHandshakeInterceptor handshakeInterceptor) {
        this.ocppWebSocketHandler = ocppWebSocketHandler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register the OCPP handler for charging stations (dual paths for compatibility)
        // Primary path (/ws/ocpp/)
        registry.addHandler(ocppWebSocketHandler, "/ws/ocpp/{stationId}")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
                
        // Alternative path (/ocpp/) to match security configuration
        registry.addHandler(ocppWebSocketHandler, "/ocpp/{stationId}")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*");
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory message broker for sending messages to clients
        // These are the prefixes for messages SENT TO clients (outbound)
        registry.enableSimpleBroker("/topic", "/queue");
        
        // This is the prefix for messages FROM clients (inbound)
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register a STOMP endpoint for the admin UI
        registry.addEndpoint("/ws/admin")
                .setAllowedOrigins("*")
                .withSockJS();
    }
} 