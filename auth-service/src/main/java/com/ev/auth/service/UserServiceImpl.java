package com.ev.auth.service;

import com.ev.auth.dto.UserResponse;
import com.ev.auth.exception.ResourceNotFoundException;
import com.ev.auth.model.Role;
import com.ev.auth.model.User;
import com.ev.auth.repository.UserRepository;
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
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::mapUserToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<UserResponse> getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapUserToResponse);
    }
    
    @Override
    public Optional<UserResponse> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapUserToResponse);
    }
    
    @Override
    @Transactional
    public UserResponse activateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User activated: {}", user.getEmail());
        return mapUserToResponse(user);
    }
    
    @Override
    @Transactional
    public UserResponse deactivateUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setEnabled(false);
        userRepository.save(user);
        log.info("User deactivated: {}", user.getEmail());
        return mapUserToResponse(user);
    }
    
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
        log.info("User deleted: {}", user.getEmail());
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
}
