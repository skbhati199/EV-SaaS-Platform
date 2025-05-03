package com.ev.auth.service;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserResponse;
import com.ev.auth.exception.AuthenticationException;
import com.ev.auth.exception.ValidationException;
import com.ev.auth.model.Role;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KeycloakService keycloakService;
    private final TwoFactorAuthService twoFactorAuthService;
    
    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        // Validate input
        validateRegistrationRequest(request);
        
        // Create user in Keycloak
        String userId = keycloakService.createUser(request);
        
        // Create user in the local database
        User user = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.valueOf(request.getRole())) // Convert String to Role enum
                .enabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered with ID: {}", savedUser.getId());
        
        return mapUserToResponse(savedUser);
    }
    
    @Override
    public TokenResponse login(String email, String password) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
        
        // Check if user is active
        if (!user.isEnabled()) {
            throw new AuthenticationException("Account is disabled");
        }
        
        // Validate password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        // Get tokens from Keycloak
        return keycloakService.getTokens(email, password);
    }
    
    // Additional login method that handles 2FA (not required by interface)
    public TokenResponse loginWith2FA(LoginRequest request, String totpCode) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
        
        // Check if user is active
        if (!user.isEnabled()) {
            throw new AuthenticationException("Account is disabled");
        }
        
        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }
        
        // Check if 2FA is enabled
        if (user.getTwoFactorAuth() != null && user.getTwoFactorAuth().isEnabled()) {
            // If 2FA code is provided, validate it
            if (totpCode == null) {
                throw new AuthenticationException("2FA code required");
            }
            
            boolean isValid = twoFactorAuthService.validate(request.getEmail(), totpCode);
            
            if (!isValid) {
                throw new AuthenticationException("Invalid 2FA code");
            }
        }
        
        // Get tokens from Keycloak
        return keycloakService.getTokens(request.getEmail(), request.getPassword());
    }
    
    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name()) // Convert Role enum to String
                .active(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    private void validateRegistrationRequest(RegisterRequest request) {
        if (userRepository.findByUsername(request.getEmail()).isPresent()) {
            throw new ValidationException("Username already exists");
        }
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }
        
        try {
            Role.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role");
        }
    }
}
