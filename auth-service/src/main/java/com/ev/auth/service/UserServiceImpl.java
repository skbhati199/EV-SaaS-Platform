package com.ev.auth.service;

import com.ev.auth.dto.UserResponse;
import com.ev.auth.exception.AuthenticationException;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    
    @Override
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapToUserResponse);
    }
    
    @Override
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToUserResponse);
    }
    
    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public UserResponse deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        
        user.setActive(false);
        User savedUser = userRepository.save(user);
        log.info("User deactivated: {}", id);
        
        return mapToUserResponse(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponse activateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
        
        user.setActive(true);
        User savedUser = userRepository.save(user);
        log.info("User activated: {}", id);
        
        return mapToUserResponse(savedUser);
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
