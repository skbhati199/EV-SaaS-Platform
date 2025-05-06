package com.ev.billingservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/billing/test")
public class TestController {

    @GetMapping("/auth")
    public ResponseEntity<Object> testAuth(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "JWT token is valid");
        
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("subject", jwt.getSubject());
        tokenInfo.put("issuer", jwt.getIssuer().toString());
        tokenInfo.put("expiration", jwt.getExpiresAt());
        tokenInfo.put("issuedAt", jwt.getIssuedAt());
        tokenInfo.put("claims", jwt.getClaims());
        
        response.put("token_info", tokenInfo);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/public")
    public ResponseEntity<Object> testPublic() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "This public endpoint is accessible without authentication");
        return ResponseEntity.ok(response);
    }
} 