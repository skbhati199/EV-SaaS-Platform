package com.ev.apigateway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to redirect to the Swagger UI from the root path
 */
@Controller
@RequestMapping("/")
public class SwaggerRedirectController {

    /**
     * Redirect to the Swagger UI page
     */
    @GetMapping
    public String redirectToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }
    
    /**
     * Redirect to the Swagger UI page from the /docs path
     */
    @GetMapping("/docs")
    public String docsToSwaggerUi() {
        return "redirect:/swagger-ui.html";
    }
    
    /**
     * Redirect to the API documentation
     */
    @GetMapping("/api-docs")
    public String apiDocs() {
        return "redirect:/v3/api-docs";
    }
} 