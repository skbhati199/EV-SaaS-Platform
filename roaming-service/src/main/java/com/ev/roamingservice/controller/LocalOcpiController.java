package com.ev.roamingservice.controller;

import com.ev.roamingservice.dto.OcpiResponse;
import com.ev.roamingservice.ocpi.module.credentials.dto.BusinessDetails;
import com.ev.roamingservice.ocpi.module.credentials.dto.Credentials;
import com.ev.roamingservice.ocpi.module.credentials.dto.CredentialsRole;
import com.ev.roamingservice.ocpi.module.credentials.dto.Image;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Simplified OCPI controller for local development
 * This controller provides mock responses without requiring database access
 */
@RestController
@Profile("local")
public class LocalOcpiController {

    private static final String PARTY_ID = "EVP";
    private static final String COUNTRY_CODE = "US";
    private static final String BASE_URL = "http://localhost:8088/ocpi";
    
    /**
     * Get OCPI versions
     */
    @GetMapping("/ocpi")
    public ResponseEntity<OcpiResponse<Map<String, Object>>> getVersions() {
        Map<String, Object> version = new HashMap<>();
        version.put("version", "2.2");
        version.put("url", BASE_URL + "/2.2");
        
        return ResponseEntity.ok(OcpiResponse.success(Collections.singletonMap("versions", List.of(version))));
    }
    
    /**
     * Get OCPI version details
     */
    @GetMapping("/ocpi/2.2")
    public ResponseEntity<OcpiResponse<Map<String, Object>>> getVersionDetails() {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, String> credentials = new HashMap<>();
        credentials.put("url", BASE_URL + "/2.2/credentials");
        
        Map<String, String> locations = new HashMap<>();
        locations.put("url", BASE_URL + "/2.2/locations");
        
        response.put("version", "2.2");
        response.put("endpoints", List.of(credentials, locations));
        
        return ResponseEntity.ok(OcpiResponse.success(response));
    }
    
    /**
     * Get credentials for OCPI client
     */
    @GetMapping("/ocpi/2.2/credentials")
    public ResponseEntity<OcpiResponse<Credentials>> getCredentials() {
        return ResponseEntity.ok(OcpiResponse.success(createCredentials()));
    }
    
    /**
     * Register credentials from OCPI client
     */
    @PostMapping("/ocpi/2.2/credentials")
    public ResponseEntity<OcpiResponse<Credentials>> registerCredentials(@RequestBody Credentials credentials) {
        return ResponseEntity.ok(OcpiResponse.success(createCredentials()));
    }
    
    /**
     * Update credentials from OCPI client
     */
    @PutMapping("/ocpi/2.2/credentials")
    public ResponseEntity<OcpiResponse<Credentials>> updateCredentials(@RequestBody Credentials credentials) {
        return ResponseEntity.ok(OcpiResponse.success(createCredentials()));
    }
    
    /**
     * Delete credentials
     */
    @DeleteMapping("/ocpi/2.2/credentials")
    public ResponseEntity<OcpiResponse<String>> deleteCredentials() {
        return ResponseEntity.ok(OcpiResponse.success("Credentials deleted"));
    }
    
    /**
     * Get locations
     */
    @GetMapping("/ocpi/2.2/locations")
    public ResponseEntity<OcpiResponse<List<Map<String, Object>>>> getLocations() {
        Map<String, Object> location = new HashMap<>();
        location.put("id", "LOC1");
        location.put("name", "EV Charging Location 1");
        location.put("address", "123 Main St");
        location.put("city", "San Francisco");
        location.put("country", "USA");
        location.put("coordinates", Map.of("latitude", 37.7749, "longitude", -122.4194));
        
        return ResponseEntity.ok(OcpiResponse.success(List.of(location)));
    }
    
    /**
     * Create mock credentials
     */
    private Credentials createCredentials() {
        String token = UUID.randomUUID().toString();
        
        BusinessDetails businessDetails = BusinessDetails.builder()
                .name("EV SaaS Platform")
                .website("https://ev-saas-platform.com")
                .logo(Image.builder()
                        .url("https://ev-saas-platform.com/logo.png")
                        .category("OPERATOR")
                        .type("png")
                        .width(512)
                        .height(512)
                        .build())
                .build();
        
        CredentialsRole role = CredentialsRole.builder()
                .role("CPO")
                .businessDetails(businessDetails)
                .partyId(PARTY_ID)
                .countryCode(COUNTRY_CODE)
                .build();
        
        return Credentials.builder()
                .token(token)
                .url(BASE_URL)
                .businessDetails(businessDetails)
                .partyId(PARTY_ID)
                .countryCode(COUNTRY_CODE)
                .roles(Collections.singletonList(role))
                .build();
    }
} 