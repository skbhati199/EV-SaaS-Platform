package com.ev.auth.service;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;
import com.ev.auth.model.User;
import com.ev.auth.model.UserRole;
import com.ev.auth.repository.UserRepository;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    
    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    
    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Create user in Keycloak
        String keycloakId = createKeycloakUser(request);
        
        // Create user in our database
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        user.setKeycloakId(keycloakId);
        
        User savedUser = userRepository.save(user);
        
        return mapToUserResponse(savedUser);
    }
    
    @Override
    public TokenResponse login(LoginRequest request) {
        try {
            // Authenticate with Keycloak
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(request.getEmail())
                    .password(request.getPassword())
                    .build();
            
            AccessTokenResponse tokenResponse = keycloak.tokenManager().getAccessToken();
            
            return TokenResponse.builder()
                    .accessToken(tokenResponse.getToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .tokenType("Bearer")
                    .expiresIn(tokenResponse.getExpiresIn())
                    .build();
        } catch (Exception e) {
            log.error("Authentication failed", e);
            throw new RuntimeException("Invalid credentials");
        }
    }
    
    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Not authenticated");
        }
        
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String email = jwt.getClaimAsString("email");
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        
        return mapToUserResponse(user);
    }
    
    private String createKeycloakUser(RegisterRequest request) {
        try {
            Keycloak keycloakAdmin = KeycloakBuilder.builder()
                    .serverUrl(authServerUrl)
                    .realm("master")
                    .clientId("admin-cli")
                    .username("admin") // Keycloak admin username
                    .password("admin") // Keycloak admin password
                    .build();
            
            // Get realm
            RealmResource realmResource = keycloakAdmin.realm(realm);
            UsersResource usersResource = realmResource.users();
            
            // Create user representation
            UserRepresentation userRepresentation = new UserRepresentation();
            userRepresentation.setEnabled(true);
            userRepresentation.setUsername(request.getEmail());
            userRepresentation.setEmail(request.getEmail());
            userRepresentation.setFirstName(request.getFirstName());
            userRepresentation.setLastName(request.getLastName());
            userRepresentation.setEmailVerified(true);
            
            // Create user
            javax.ws.rs.core.Response response = usersResource.create(userRepresentation);
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            
            // Set password
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(request.getPassword());
            usersResource.get(userId).resetPassword(passwordCred);
            
            // Assign role
            RoleRepresentation roleRepresentation = realmResource.roles().get(request.getRole().name()).toRepresentation();
            usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRepresentation));
            
            return userId;
        } catch (Exception e) {
            log.error("Error creating Keycloak user", e);
            throw new RuntimeException("Failed to create user in Keycloak");
        }
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
