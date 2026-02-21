package com.sdl.controller;

import com.sdl.dto.request.CreateUserRequestDTO;
import com.sdl.dto.response.ApiResponseDTO;
import com.sdl.dto.response.UserResponseDTO;
import com.sdl.entity.User;
import com.sdl.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserService userService;

    public UserManagementController(UserService userService) {
        this.userService = userService;
    }

    // ==========================
    // CREATE USER
    // ==========================
    @PostMapping("/create")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(
            @Valid @RequestBody CreateUserRequestDTO request,
            @AuthenticationPrincipal User creator) {

        UserResponseDTO response = userService.createUser(request, creator);

        return ResponseEntity.ok(
                ApiResponseDTO.success("User created successfully", response)
        );
    }

    // ==========================
    // GET USER BY ID
    // ==========================
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUser(
            @PathVariable Long id) {

        UserResponseDTO response = userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponseDTO.success("User fetched", response)
        );
    }

    // ==========================
    // DEACTIVATE USER
    // ==========================
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponseDTO<Void>> deactivateUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User performer) {

        userService.deactivateUser(id, performer);

        return ResponseEntity.ok(
                ApiResponseDTO.success("User deactivated", null)
        );
    }
}