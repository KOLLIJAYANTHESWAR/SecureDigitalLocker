package com.sdl.controller;

import com.sdl.dto.request.ProfileUpdateDTO;
import com.sdl.dto.response.ApiResponseDTO;
import com.sdl.dto.response.ProfileResponseDTO;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final ProfileService profileService;

    public UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    // View own profile
    @GetMapping("/profile")
    public ResponseEntity<ApiResponseDTO<ProfileResponseDTO>> getProfile(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        ProfileResponseDTO response = profileService.getProfile(user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Profile fetched", response)
        );
    }

    // Update own profile
    @PutMapping(value = "/profile", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponseDTO<ProfileResponseDTO>> updateProfile(
            @Valid @RequestBody ProfileUpdateDTO request,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        ProfileResponseDTO response =
                profileService.updateProfile(user, request);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Profile updated", response)
        );
    }

    // Upload profile picture
    @PostMapping(value = "/profile/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponseDTO<ProfileResponseDTO>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        ProfileResponseDTO response =
                profileService.uploadProfilePicture(user, file);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Profile picture uploaded", response)
        );
    }
}