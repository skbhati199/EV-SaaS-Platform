package com.ev.auth.controller;

import com.ev.auth.config.OpenApiConfig;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.OpenAPI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to manually expose OpenAPI documentation
 * This is a workaround for the SpringDoc issue
 */
@RestController
@RequestMapping("/api/v1/docs")
@Hidden
@Slf4j
public class ApiDocsController {

    private final OpenApiConfig openApiConfig;

    @Autowired
    public ApiDocsController(OpenApiConfig openApiConfig) {
        this.openApiConfig = openApiConfig;
    }

    @GetMapping(value = "/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("permitAll()")
    public ResponseEntity<OpenAPI> getApiDocs() {
        try {
            log.info("Generating custom OpenAPI docs");
            OpenAPI openAPI = openApiConfig.authServiceOpenAPI();
            log.info("OpenAPI docs generated successfully");
            return ResponseEntity.ok(openAPI);
        } catch (Exception e) {
            log.error("Error generating OpenAPI docs: {}", e.getMessage(), e);
            throw e;
        }
    }
} 