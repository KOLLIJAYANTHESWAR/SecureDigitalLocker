package com.sdl.controller;

import com.sdl.dto.request.CreateUserRequestDTO;
import com.sdl.dto.response.ApiResponseDTO;
import com.sdl.dto.response.AuditLogResponseDTO;
import com.sdl.dto.response.UserResponseDTO;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.service.AuditService;
import com.sdl.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

        private final UserService userService;
        private final AuditService auditService;

        public AdminController(UserService userService,
                        AuditService auditService) {
                this.userService = userService;
                this.auditService = auditService;
        }

        // Create HR
        @PostMapping("/create-hr")
        public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createHr(
                        @Valid @RequestBody CreateUserRequestDTO request,
                        @AuthenticationPrincipal User admin) {

                if (admin == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                request.setRole(CreateUserRequestDTO.Role.HR);

                UserResponseDTO response = userService.createUser(request, admin);

                return ResponseEntity.ok(
                                ApiResponseDTO.success(
                                                "HR user created successfully. Pending approval.",
                                                response));
        }

        // Layoff any user
        @PutMapping("/layoff/{userId}")
        public ResponseEntity<ApiResponseDTO<String>> layoffUser(
                        @PathVariable Long userId,
                        @AuthenticationPrincipal User admin) {

                if (admin == null) {
                        throw new AccessDeniedException("Unauthorized");
                }

                userService.deactivateUser(userId, admin);

                return ResponseEntity.ok(
                                ApiResponseDTO.success("User has been deactivated"));
        }

        // View all HR users
        @GetMapping("/hr-users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllHrUsers() {

                List<UserResponseDTO> users = userService.getUsersByRole(User.Role.HR.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("HR users fetched", users));
        }

        // View all Managers
        @GetMapping("/managers")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllManagers() {

                List<UserResponseDTO> users = userService.getUsersByRole(User.Role.MANAGER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Managers fetched", users));
        }

        // View all Users
        @GetMapping("/users")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllUsers() {

                List<UserResponseDTO> users = userService.getUsersByRole(User.Role.USER.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Users fetched", users));
        }

        // View all pending HR approvals
        @GetMapping("/pending-hr")
        public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getPendingHr() {

                List<UserResponseDTO> users = userService.getPendingUsersByRole(User.Role.HR.name());

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Pending HR users fetched", users));
        }

        // View audit logs
        @GetMapping("/audit-logs")
        public ResponseEntity<ApiResponseDTO<List<AuditLogResponseDTO>>> getAuditLogs() {

                List<AuditLogResponseDTO> logs = auditService.getAllLogs();

                return ResponseEntity.ok(
                                ApiResponseDTO.success("Audit logs fetched", logs));
        }

        // View specific user
        @GetMapping("/user/{userId}")
        public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUser(
                        @PathVariable Long userId) {

                UserResponseDTO user = userService.getUserById(userId);

                return ResponseEntity.ok(
                                ApiResponseDTO.success("User fetched", user));
        }
}