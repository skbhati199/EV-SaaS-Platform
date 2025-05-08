package com.ev.station.ocpp;

import com.ev.station.service.ChargingStationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StationHandshakeInterceptor implements HandshakeInterceptor {

    private final ChargingStationService stationService;
    
    // Support both paths from SecurityConfig and WebSocketConfig
    private static final UriTemplate WS_OCPP_TEMPLATE = new UriTemplate("/ws/ocpp/{stationId}");
    private static final UriTemplate OCPP_TEMPLATE = new UriTemplate("/ocpp/{stationId}");
    private static final String SUPPORTED_OCPP_VERSION = "1.6";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                  WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String path = request.getURI().getPath();
        Map<String, String> uriVariables;
        String stationId;
        
        // Try both URI templates
        uriVariables = WS_OCPP_TEMPLATE.match(path);
        if (uriVariables == null || uriVariables.isEmpty()) {
            uriVariables = OCPP_TEMPLATE.match(path);
            if (uriVariables == null || uriVariables.isEmpty()) {
                log.warn("Invalid URI path format: {}", path);
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                return false;
            }
        }
        
        stationId = uriVariables.get("stationId");
        log.info("WebSocket connection attempt from station ID: {}", stationId);

        // Store station ID in attributes
        attributes.put("stationId", stationId);

        // Validate Sec-WebSocket-Protocol header
        String protocol = "ocpp1.6";
        if (!request.getHeaders().containsKey("Sec-WebSocket-Protocol") || 
            !request.getHeaders().get("Sec-WebSocket-Protocol").contains(protocol)) {
            log.warn("Missing or invalid Sec-WebSocket-Protocol header. Expected to contain: {}", protocol);
            // We'll still allow the connection for testing purposes
            log.info("Continuing with connection despite protocol mismatch for testing purposes");
        }

        try {
            // Check if station exists, if not, this will create it in "pending" state
            stationService.registerOrUpdateStation(stationId);
            return true;
        } catch (Exception e) {
            log.error("Error during handshake for station {}: {}", stationId, e.getMessage(), e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
        // Nothing to do after handshake
        log.info("After handshake");
    }
} 