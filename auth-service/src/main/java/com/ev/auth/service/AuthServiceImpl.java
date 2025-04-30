package com.ev.auth.service;

import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;
import com.ev.auth.exception.AuthenticationException;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    
    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            log.error("User with email {} already exists", request.getEmail());
            throw new AuthenticationException("User with this email already exists");
        }
        
        // Create user in Keycloak
        String keycloakId = keycloakService.createUser(request);
        
        // Create user in our database
        User user = User.builder()
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(request.getRole())
                .keycloakId(keycloakId)
                .active(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        return mapToUserResponse(savedUser);
    }
    
    @Override
    public TokenResponse login(String email, String password) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));
        
        // Check if user is active
        if (!user.isActive()) {
            log.error("Inactive user attempted to login: {}", email);
            throw new AuthenticationException("User account is inactive");
        }
        
        // Authenticate with Keycloak and get tokens
        TokenResponse tokenResponse = keycloakService.getTokens(email, password);
        log.info("User logged in successfully: {}", email);
        
        return tokenResponse;
    }
    
    /**
     * Maps a User entity to UserResponse DTO
     * @param user User entity
     * @return UserResponse DTO
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
