package com.ev.userservice.service;

import com.ev.userservice.dto.CreateUserRequest;
import com.ev.userservice.dto.UpdateUserRequest;
import com.ev.userservice.dto.UserDto;
import com.ev.userservice.model.Role;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto getUserById(UUID id);
    UserDto getUserByEmail(String email);
    List<UserDto> getUsersByRole(Role role);
    UserDto createUser(CreateUserRequest request);
    UserDto updateUser(UUID id, UpdateUserRequest request);
    void deleteUser(UUID id);
    boolean existsByEmail(String email);
    UserDto disableUser(UUID id);
    UserDto enableUser(UUID id);
} 