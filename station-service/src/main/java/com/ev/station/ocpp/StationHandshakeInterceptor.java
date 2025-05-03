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
    private static final UriTemplate STATION_ID_TEMPLATE = new UriTemplate("/ocpp/{stationId}/{ocppVersion}");
    private static final String SUPPORTED_OCPP_VERSION = "1.6";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                  WebSocketHandler wsHandler, Map<String, Object> attributes) {
        Map<String, String> uriVariables = STATION_ID_TEMPLATE.match(request.getURI().getPath());
        String stationId = uriVariables.get("stationId");
        String ocppVersion = uriVariables.get("ocppVersion");

        log.info("WebSocket connection attempt from station ID: {}, OCPP version: {}", stationId, ocppVersion);

        // Validate OCPP version
        if (!SUPPORTED_OCPP_VERSION.equals(ocppVersion)) {
            log.warn("Unsupported OCPP version: {}", ocppVersion);
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        // Store station ID and OCPP version in attributes
        attributes.put("stationId", stationId);
        attributes.put("ocppVersion", ocppVersion);

        // Validate Sec-WebSocket-Protocol header
        String protocol = String.format("ocpp%s", ocppVersion.replace(".", ""));
        if (!request.getHeaders().containsKey("Sec-WebSocket-Protocol") || 
            !request.getHeaders().get("Sec-WebSocket-Protocol").contains(protocol)) {
            log.warn("Invalid or missing Sec-WebSocket-Protocol header. Expected: {}", protocol);
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
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
        if (exception != null) {
            log.error("Error during handshake completion: {}", exception.getMessage(), exception);
        }
    }
} 