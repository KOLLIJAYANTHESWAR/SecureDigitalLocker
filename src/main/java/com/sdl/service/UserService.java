package com.sdl.service;

import com.sdl.dto.request.CreateUserRequestDTO;
import com.sdl.dto.response.UserResponseDTO;
import com.sdl.entity.User;

import java.util.List;

public interface UserService {

    UserResponseDTO createUser(CreateUserRequestDTO request, User creator);

    void deactivateUser(Long userId, User performer);

    UserResponseDTO getUserById(Long userId);

    List<UserResponseDTO> getUsersByRole(String role);

    List<UserResponseDTO> getActiveUsersByRole(String role);

    List<UserResponseDTO> getUsersCreatedBy(Long creatorId);

    List<UserResponseDTO> getPendingUsersByRole(String role);

    User findByEmail(String email);

    User findById(Long id);
}