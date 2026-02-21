package com.sdl.controller;

import com.sdl.dto.request.CreateUserRequestDTO;
import com.sdl.dto.response.ApiResponseDTO;
import com.sdl.dto.response.UserResponseDTO;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
public class ManagerController {

        private final UserService userService;

        public ManagerController(UserService userService) {
                this.userService = userService;
        }

        // Create User
        @PostMapping("/create-user")
        public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(
                        @Valid @RequestBody CreateUserRequestDTO request,
                        @AuthenticationPrincipal User manager) {

                if (manager == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                request.setRole(CreateUserRequestDTO.Role.USER);

                UserResponseDTO response = userService.createUser(request, manager);

                return ResponseEntity.ok(
                                ApiResponseDTO.success(
                                                "User created successfully. Pending approval.",
                                                response));
        }

        // View Users
        @GetMapping("/users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getUsers() {

                List<UserResponseDTO> users = userService.getUsersByRole(User.Role.USER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Users fetched", users));
        }

        // View pending Users
        @GetMapping("/pending-users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getPendingUsers() {

                List<UserResponseDTO> users = userService.getPendingUsersByRole(User.Role.USER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Pending users fetched", users));
        }

        // View users created by this Manager
        @GetMapping("/my-created-users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getMyCreatedUsers(
                        @AuthenticationPrincipal User manager) {

                if (manager == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                List<UserResponseDTO> users = userService.getUsersCreatedBy(manager.getId());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Created users fetched", users));
        }
}