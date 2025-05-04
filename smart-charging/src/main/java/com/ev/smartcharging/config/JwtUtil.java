package com.ev.smartcharging.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Collections;
import java.util.List;

@Component
public class JwtUtil {

    /**
     * Get the current JWT from the security context
     */
    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            return (Jwt) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * Get the current authenticated user ID
     */
    public String getCurrentUserId() {
        Jwt jwt = getCurrentJwt();
        if (jwt != null) {
            return jwt.getSubject();
        }
        return null;
    }

    /**
     * Get user roles from JWT
     */
    @SuppressWarnings("unchecked")
    public List<String> getUserRoles() {
        Jwt jwt = getCurrentJwt();
        if (jwt != null) {
            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                return (List<String>) realmAccess.getOrDefault("roles", Collections.emptyList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(String role) {
        return getUserRoles().contains(role);
    }
} 