package com.ev.auth.service;

import com.ev.auth.dto.LoginRequest;
import com.ev.auth.dto.RegisterRequest;
import com.ev.auth.dto.TokenResponse;
import com.ev.auth.dto.UserDto;
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
    public UserDto registerUser(RegisterRequest request) {
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
        
        return mapUserToDto(savedUser);
    }
    
    @Override
    public TokenResponse login(LoginRequest request) {
        // Find user by username
        User user = userRepository.findByUsername(request.getUsername())
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
            if (request.getTotpCode() == null) {
                throw new AuthenticationException("2FA code required");
            }
            
            boolean isValid = twoFactorAuthService.validateCode(
                    user.getTwoFactorAuth().getSecret(),
                    request.getTotpCode()
            );
            
            if (!isValid) {
                throw new AuthenticationException("Invalid 2FA code");
            }
        }
        
        // Get tokens from Keycloak
        return keycloakService.getTokens(request.getUsername(), request.getPassword());
    }
    
    @Override
    public boolean validateToken(String token) {
        return keycloakService.validateToken(token);
    }
    
    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name()) // Convert Role enum to String
                .enabled(user.isEnabled())
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
