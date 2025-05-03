package com.ev.roamingservice.ocpi.version.controller;

import com.ev.roamingservice.config.OcpiConfig;
import com.ev.roamingservice.dto.OcpiResponse;
import com.ev.roamingservice.ocpi.version.dto.VersionDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling OCPI version endpoints
 */
@RestController
@RequestMapping("${ocpi.base-path}")
public class VersionController {

    private final OcpiConfig ocpiConfig;
    private final Map<String, List<VersionDetails.Endpoint>> versionEndpoints;

    @Autowired
    public VersionController(OcpiConfig ocpiConfig) {
        this.ocpiConfig = ocpiConfig;
        this.versionEndpoints = initializeVersionEndpoints();
    }

    /**
     * Initialize the available endpoints for each OCPI version
     * @return a map of version to endpoints
     */
    private Map<String, List<VersionDetails.Endpoint>> initializeVersionEndpoints() {
        Map<String, List<VersionDetails.Endpoint>> endpoints = new HashMap<>();
        
        // OCPI 2.2 endpoints
        List<VersionDetails.Endpoint> v2_2_endpoints = new ArrayList<>();
        v2_2_endpoints.add(new VersionDetails.Endpoint("credentials", ocpiConfig.getRole(), 
                ocpiConfig.getFullUrl("/2.2/credentials")));
        v2_2_endpoints.add(new VersionDetails.Endpoint("locations", ocpiConfig.getRole(), 
                ocpiConfig.getFullUrl("/2.2/locations")));
        // Commented out for future implementation
        // v2_2_endpoints.add(new VersionDetails.Endpoint("tariffs", ocpiConfig.getRole(), 
        //        ocpiConfig.getFullUrl("/2.2/tariffs")));
        // v2_2_endpoints.add(new VersionDetails.Endpoint("sessions", ocpiConfig.getRole(), 
        //        ocpiConfig.getFullUrl("/2.2/sessions")));
        // v2_2_endpoints.add(new VersionDetails.Endpoint("cdrs", ocpiConfig.getRole(), 
        //        ocpiConfig.getFullUrl("/2.2/cdrs")));
        
        endpoints.put("2.2", v2_2_endpoints);
        
        return endpoints;
    }

    /**
     * Get all supported versions
     * @return ResponseEntity with a list of supported versions
     */
    @GetMapping
    public ResponseEntity<OcpiResponse<List<Map<String, String>>>> getVersions() {
        List<Map<String, String>> versions = new ArrayList<>();
        
        for (String version : versionEndpoints.keySet()) {
            Map<String, String> versionInfo = new HashMap<>();
            versionInfo.put("version", version);
            versionInfo.put("url", ocpiConfig.getFullUrl("/" + version));
            versions.add(versionInfo);
        }
        
        return ResponseEntity.ok(OcpiResponse.success(versions));
    }

    /**
     * Get details for a specific version
     * @param version the version to get details for
     * @return ResponseEntity with version details
     */
    @GetMapping("/{version}")
    public ResponseEntity<OcpiResponse<VersionDetails>> getVersionDetails(@PathVariable String version) {
        if (!versionEndpoints.containsKey(version)) {
            return ResponseEntity.status(404).body(OcpiResponse.clientError("Version not found"));
        }
        
        VersionDetails versionDetails = new VersionDetails(version, versionEndpoints.get(version));
        return ResponseEntity.ok(OcpiResponse.success(versionDetails));
    }
} 