package com.ev.station.controller;

import com.ev.station.dto.EVSERegistrationRequest;
import com.ev.station.dto.EVSEResponse;
import com.ev.station.service.EVSEService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evse")
@RequiredArgsConstructor
@Slf4j
public class EVSEController {

    private final EVSEService evseService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO')")
    public ResponseEntity<EVSEResponse> registerEVSE(
            @Valid @RequestBody EVSERegistrationRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        
        // Extract user ID from JWT token
        UUID ownerId = UUID.fromString(jwt.getSubject());
        log.info("Registering new EVSE with ID: {} for owner: {}", request.getEvseId(), ownerId);
        
        EVSEResponse response = evseService.registerEVSE(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<EVSEResponse> getEVSEById(@PathVariable UUID id) {
        log.info("Fetching EVSE with ID: {}", id);
        
        Optional<EVSEResponse> evseResponse = evseService.getEVSEById(id);
        return evseResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/evse-id/{evseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CPO', 'EMSP')")
    public ResponseEntity<EVSEResponse> getEVSEByEvseId(@PathVariable String evseId) {
        log.info("Fetching EVSE with EVSE ID: {}", evseId);
        
        Optional<EVSEResponse> evseResponse = evseService.getEVSEByEvseId(evseId);
        return evseResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CPO') and #ownerId == authentication.principal.subject)")
    public ResponseEntity<List<EVSEResponse>> getEVSEsByOwnerId(@PathVariable UUID ownerId) {
        log.info("Fetching all EVSEs for owner: {}", ownerId);
        
        List<EVSEResponse> evseResponses = evseService.getEVSEsByOwnerId(ownerId);
        return ResponseEntity.ok(evseResponses);
    }
    
    @PutMapping("/heartbeat/{evseId}")
    public ResponseEntity<EVSEResponse> updateHeartbeat(@PathVariable String evseId) {
        log.debug("Received heartbeat from EVSE: {}", evseId);
        
        Optional<EVSEResponse> evseResponse = evseService.updateHeartbeat(evseId);
        return evseResponse
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
