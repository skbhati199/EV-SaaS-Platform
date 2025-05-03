package com.ev.roamingservice;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * A very simple HTTP server using Java's built-in HTTP server
 */
public class SimpleHttpServer {

    // Configuration defaults
    private static final int DEFAULT_PORT = 8088;
    private static final String DEFAULT_BASE_URL = "http://localhost:8088";
    private static final String DEFAULT_PARTY_ID = "EVP";
    private static final String DEFAULT_COUNTRY_CODE = "US";
    private static final String DEFAULT_ROLE = "CPO";

    // Actual configuration (can be overridden by environment variables)
    private static int port;
    private static String baseUrl;
    private static String partyId;
    private static String countryCode;
    private static String role;

    public static void main(String[] args) throws IOException {
        // Load configuration from environment variables
        loadConfig();
        
        // Create and configure the server
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        
        // Create a context for health checks
        server.createContext("/health", new HealthHandler());
        
        // Create a context for OCPI endpoints
        server.createContext("/ocpi", new OcpiHandler());
        
        // Set the executor
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        // Start the server
        server.start();
        
        System.out.println("Server started on port " + port);
        System.out.println("Base URL: " + baseUrl);
        System.out.println("Party ID: " + partyId);
        System.out.println("Country Code: " + countryCode);
        System.out.println("Role: " + role);
    }
    
    /**
     * Load configuration from environment variables
     */
    private static void loadConfig() {
        String portEnv = System.getenv("SERVER_PORT");
        port = (portEnv != null) ? Integer.parseInt(portEnv) : DEFAULT_PORT;
        
        String baseUrlEnv = System.getenv("OCPI_EXTERNAL_URL");
        baseUrl = (baseUrlEnv != null) ? baseUrlEnv : DEFAULT_BASE_URL;
        
        String partyIdEnv = System.getenv("OCPI_PARTY_ID");
        partyId = (partyIdEnv != null) ? partyIdEnv : DEFAULT_PARTY_ID;
        
        String countryCodeEnv = System.getenv("OCPI_COUNTRY_CODE");
        countryCode = (countryCodeEnv != null) ? countryCodeEnv : DEFAULT_COUNTRY_CODE;
        
        String roleEnv = System.getenv("OCPI_ROLE");
        role = (roleEnv != null) ? roleEnv : DEFAULT_ROLE;
    }
    
    /**
     * Handler for health checks
     */
    static class HealthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "{\"status\":\"UP\",\"service\":\"roaming-service-simple\",\"timestamp\":" + System.currentTimeMillis() + "}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
    
    /**
     * Handler for OCPI endpoints
     */
    static class OcpiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            
            String response;
            
            if (path.equals("/ocpi")) {
                // OCPI version endpoint
                response = "{\"data\":{\"versions\":[{\"version\":\"2.2\",\"url\":\"" + baseUrl + "/ocpi/2.2\"}]},\"status_code\":1000,\"status_message\":\"Success\",\"timestamp\":\"" + java.time.ZonedDateTime.now() + "\"}";
            } else if (path.equals("/ocpi/2.2")) {
                // OCPI version details endpoint
                response = "{\"data\":{\"version\":\"2.2\",\"endpoints\":[{\"identifier\":\"credentials\",\"url\":\"" + baseUrl + "/ocpi/2.2/credentials\"}]},\"status_code\":1000,\"status_message\":\"Success\",\"timestamp\":\"" + java.time.ZonedDateTime.now() + "\"}";
            } else if (path.equals("/ocpi/2.2/credentials")) {
                // OCPI credentials endpoint
                if (method.equals("GET") || method.equals("POST") || method.equals("PUT")) {
                    response = "{\"data\":{\"token\":\"" + java.util.UUID.randomUUID() + "\",\"url\":\"" + baseUrl + "/ocpi\",\"roles\":[{\"role\":\"" + role + "\",\"party_id\":\"" + partyId + "\",\"country_code\":\"" + countryCode + "\"}]},\"status_code\":1000,\"status_message\":\"Success\",\"timestamp\":\"" + java.time.ZonedDateTime.now() + "\"}";
                } else if (method.equals("DELETE")) {
                    response = "{\"data\":\"Credentials deleted\",\"status_code\":1000,\"status_message\":\"Success\",\"timestamp\":\"" + java.time.ZonedDateTime.now() + "\"}";
                } else {
                    response = "{\"status_code\":2001,\"status_message\":\"Method not allowed\",\"timestamp\":\"" + java.time.ZonedDateTime.now() + "\"}";
                    exchange.sendResponseHeaders(405, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                    return;
                }
            } else {
                // Not found
                response = "{\"status_code\":2004,\"status_message\":\"Endpoint not found\",\"timestamp\":\"" + java.time.ZonedDateTime.now() + "\"}";
                exchange.sendResponseHeaders(404, response.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                return;
            }
            
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
} 