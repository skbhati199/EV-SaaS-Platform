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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        validateRegistrationRequest(request);
        
        // Create user entity
        User user = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(Role.valueOf(request.getRole()))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();
        
        // Save to database
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Return user response
        return mapToUserResponse(savedUser);
    }
    
    @Override
    @Transactional
    public TokenResponse login(String email, String password) {
        log.info("Login attempt for user: {}", email);
        
        User user = userRepository.findByUsername(email)
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));
        
        // Check password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Invalid password for user: {}", email);
            throw new AuthenticationException("Invalid credentials");
        }
        
        // Check if account is enabled
        if (!user.isEnabled()) {
            log.warn("Account disabled for user: {}", email);
            throw new AuthenticationException("Account is disabled");
        }
        
        // Generate access token 
        String accessToken = jwtService.generateToken(user);
        
        // Create refresh token and store in database via RefreshTokenService
        String refreshToken = refreshTokenService.createRefreshToken(user.getId().toString());
        
        log.info("User logged in successfully: {}", email);
        
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600) // 1 hour in seconds
                .build();
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .active(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
    
    private void validateRegistrationRequest(RegisterRequest request) {
        if (userRepository.findByUsername(request.getEmail()).isPresent()) {
            throw new ValidationException("Username already exists");
        }
        
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ValidationException("Email already exists");
        }
        
        // Provide a default role if none specified
        if (request.getRole() == null || request.getRole().isEmpty()) {
            request.setRole("USER");
        }
        
        try {
            Role.valueOf(request.getRole());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role: " + request.getRole());
        }
    }

    @Override
    public UUID getUserIdFromToken(String token) {
        try {
            // Use JwtService to extract user ID
            return jwtService.extractUserId(token);
        } catch (Exception e) {
            log.error("Error extracting user ID from token", e);
            throw new RuntimeException("Invalid token");
        }
    }
}
