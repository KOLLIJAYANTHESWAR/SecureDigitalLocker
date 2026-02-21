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
@RequestMapping("/api/hr")
public class HrController {

        private final UserService userService;

        public HrController(UserService userService) {
                this.userService = userService;
        }

        // Create Manager
        @PostMapping("/create-manager")
        public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createManager(
                        @Valid @RequestBody CreateUserRequestDTO request,
                        @AuthenticationPrincipal User hr) {

                if (hr == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                request.setRole(CreateUserRequestDTO.Role.MANAGER);

                UserResponseDTO response = userService.createUser(request, hr);

                return ResponseEntity.ok(
                                ApiResponseDTO.success(
                                                "Manager created successfully. Pending approval.",
                                                response));
        }

        // Create User
        @PostMapping("/create-user")
        public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(
                        @Valid @RequestBody CreateUserRequestDTO request,
                        @AuthenticationPrincipal User hr) {

                if (hr == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                request.setRole(CreateUserRequestDTO.Role.USER);

                UserResponseDTO response = userService.createUser(request, hr);

                return ResponseEntity.ok(
                                ApiResponseDTO.success(
                                                "User created successfully. Pending approval.",
                                                response));
        }

        // Layoff Manager or User
        @PutMapping("/layoff/{userId}")
        public ResponseEntity<ApiResponseDTO<String>> layoffUser(
                        @PathVariable Long userId,
                        @AuthenticationPrincipal User hr) {

                if (hr == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                userService.deactivateUser(userId, hr);

                return ResponseEntity.ok(
                                ApiResponseDTO.success("User has been deactivated"));
        }

        // View all Managers
        @GetMapping("/managers")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getManagers() {

                List<UserResponseDTO> users = userService.getUsersByRole(User.Role.MANAGER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Managers fetched", users));
        }

        // View all Users
        @GetMapping("/users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getUsers() {

                List<UserResponseDTO> users = userService.getUsersByRole(User.Role.USER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Users fetched", users));
        }

        // View pending Managers
        @GetMapping("/pending-managers")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getPendingManagers() {

                List<UserResponseDTO> users = userService.getPendingUsersByRole(User.Role.MANAGER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Pending managers fetched", users));
        }

        // View pending Users
        @GetMapping("/pending-users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getPendingUsers() {

                List<UserResponseDTO> users = userService.getPendingUsersByRole(User.Role.USER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Pending users fetched", users));
        }

        // View users created by this HR
        @GetMapping("/my-created-users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getMyCreatedUsers(
                        @AuthenticationPrincipal User hr) {

                if (hr == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                List<UserResponseDTO> users = userService.getUsersCreatedBy(hr.getId());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Created users fetched", users));
        }
}