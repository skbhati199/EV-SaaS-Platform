package com.ev.userservice.service.impl;

import com.ev.userservice.dto.CreateUserRequest;
import com.ev.userservice.dto.UpdateUserRequest;
import com.ev.userservice.dto.UserDto;
import com.ev.userservice.dto.event.UserEvent;
import com.ev.userservice.dto.event.WalletEvent;
import com.ev.userservice.model.Role;
import com.ev.userservice.model.User;
import com.ev.userservice.model.Wallet;
import com.ev.userservice.repository.UserRepository;
import com.ev.userservice.repository.WalletRepository;
import com.ev.userservice.service.KafkaProducerService;
import com.ev.userservice.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;
    
    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(UUID id) {
        return userRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserDto getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapToDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<UserDto> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use: " + request.getEmail());
        }
        
        // Create the user entity
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .role(request.getRole())
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        User savedUser = userRepository.save(user);
        
        // Create a wallet for the user
        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .balance(BigDecimal.ZERO)
                .currency("USD")
                .build();
        
        Wallet savedWallet = walletRepository.save(wallet);
        
        // Publish user created event
        try {
            UserEvent userEvent = kafkaProducerService.createUserEvent(savedUser, UserEvent.UserEventType.CREATED);
            kafkaProducerService.sendUserEvent(userEvent);
            
            // Publish wallet created event
            WalletEvent walletEvent = WalletEvent.builder()
                    .eventId(UUID.randomUUID())
                    .userId(savedUser.getId())
                    .walletId(savedWallet.getId())
                    .eventType(WalletEvent.WalletEventType.CREATED)
                    .timestamp(LocalDateTime.now())
                    .newBalance(savedWallet.getBalance())
                    .description("Wallet created for new user")
                    .build();
            
            kafkaProducerService.sendWalletEvent(walletEvent);
        } catch (Exception e) {
            log.error("Failed to publish user/wallet creation events", e);
            // We don't want to fail the user creation if event publishing fails
        }
        
        return mapToDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        boolean profileUpdated = false;
        
        if (request.getFirstName() != null && !request.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(request.getFirstName());
            profileUpdated = true;
        }
        
        if (request.getLastName() != null && !request.getLastName().equals(user.getLastName())) {
            user.setLastName(request.getLastName());
            profileUpdated = true;
        }
        
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().equals(user.getPhoneNumber())) {
            user.setPhoneNumber(request.getPhoneNumber());
            profileUpdated = true;
        }
        
        if (request.getAddress() != null && !request.getAddress().equals(user.getAddress())) {
            user.setAddress(request.getAddress());
            profileUpdated = true;
        }
        
        if (request.getCity() != null && !request.getCity().equals(user.getCity())) {
            user.setCity(request.getCity());
            profileUpdated = true;
        }
        
        if (request.getCountry() != null && !request.getCountry().equals(user.getCountry())) {
            user.setCountry(request.getCountry());
            profileUpdated = true;
        }
        
        if (request.getPostalCode() != null && !request.getPostalCode().equals(user.getPostalCode())) {
            user.setPostalCode(request.getPostalCode());
            profileUpdated = true;
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        
        // Publish user updated event if profile was updated
        if (profileUpdated) {
            try {
                UserEvent userEvent = kafkaProducerService.createUserEvent(
                        updatedUser, UserEvent.UserEventType.PROFILE_UPDATED);
                kafkaProducerService.sendUserEvent(userEvent);
            } catch (Exception e) {
                log.error("Failed to publish user update event", e);
            }
        }
        
        return mapToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        userRepository.deleteById(id);
        
        // Publish user deleted event
        try {
            UserEvent userEvent = kafkaProducerService.createUserEvent(user, UserEvent.UserEventType.DELETED);
            kafkaProducerService.sendUserEvent(userEvent);
        } catch (Exception e) {
            log.error("Failed to publish user deletion event", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDto disableUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        user.setEnabled(false);
        user.setUpdatedAt(LocalDateTime.now());
        
        User disabledUser = userRepository.save(user);
        
        // Publish user disabled event
        try {
            UserEvent userEvent = kafkaProducerService.createUserEvent(
                    disabledUser, UserEvent.UserEventType.ACCOUNT_DISABLED);
            kafkaProducerService.sendUserEvent(userEvent);
        } catch (Exception e) {
            log.error("Failed to publish user disabled event", e);
        }
        
        return mapToDto(disabledUser);
    }

    @Override
    @Transactional
    public UserDto enableUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        user.setEnabled(true);
        user.setUpdatedAt(LocalDateTime.now());
        
        User enabledUser = userRepository.save(user);
        
        // Publish user enabled event
        try {
            UserEvent userEvent = kafkaProducerService.createUserEvent(
                    enabledUser, UserEvent.UserEventType.ACCOUNT_ENABLED);
            kafkaProducerService.sendUserEvent(userEvent);
        } catch (Exception e) {
            log.error("Failed to publish user enabled event", e);
        }
        
        return mapToDto(enabledUser);
    }
    
    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .country(user.getCountry())
                .postalCode(user.getPostalCode())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
} 