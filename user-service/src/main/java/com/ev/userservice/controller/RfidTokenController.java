package com.ev.userservice.controller;

import com.ev.userservice.dto.CreateRfidTokenRequest;
import com.ev.userservice.dto.RfidTokenDto;
import com.ev.userservice.service.RfidTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/{userId}/rfid-tokens")
@RequiredArgsConstructor
public class RfidTokenController {

    private final RfidTokenService rfidTokenService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
    public ResponseEntity<List<RfidTokenDto>> getAllTokensByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(rfidTokenService.getAllTokensByUserId(userId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
    public ResponseEntity<RfidTokenDto> getTokenById(@PathVariable UUID userId, @PathVariable UUID id) {
        RfidTokenDto token = rfidTokenService.getTokenById(id);
        if (!token.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
    public ResponseEntity<RfidTokenDto> createToken(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateRfidTokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rfidTokenService.createToken(userId, request));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
    public ResponseEntity<RfidTokenDto> activateToken(@PathVariable UUID userId, @PathVariable UUID id) {
        RfidTokenDto token = rfidTokenService.getTokenById(id);
        if (!token.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(rfidTokenService.activateToken(id));
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
    public ResponseEntity<RfidTokenDto> deactivateToken(@PathVariable UUID userId, @PathVariable UUID id) {
        RfidTokenDto token = rfidTokenService.getTokenById(id);
        if (!token.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(rfidTokenService.deactivateToken(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or principal.id == #userId")
    public ResponseEntity<Void> deleteToken(@PathVariable UUID userId, @PathVariable UUID id) {
        RfidTokenDto token = rfidTokenService.getTokenById(id);
        if (!token.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        rfidTokenService.deleteToken(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/validate/{tokenValue}")
    public ResponseEntity<Boolean> validateToken(@PathVariable String tokenValue) {
        return ResponseEntity.ok(rfidTokenService.isTokenValid(tokenValue));
    }
} 