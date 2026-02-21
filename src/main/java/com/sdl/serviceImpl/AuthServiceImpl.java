package com.sdl.serviceImpl;

import com.sdl.dto.request.LoginRequestDTO;
import com.sdl.dto.request.RegisterRequestDTO;
import com.sdl.dto.response.AuthResponseDTO;
import com.sdl.entity.AuditLog;
import com.sdl.entity.Profile;
import com.sdl.entity.User;
import com.sdl.exception.BusinessException;
import com.sdl.exception.ResourceNotFoundException;
import com.sdl.repository.ProfileRepository;
import com.sdl.repository.UserRepository;
import com.sdl.security.JwtService;
import com.sdl.service.AuditService;
import com.sdl.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final ProfileRepository profileRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtService jwtService;
        private final AuditService auditService;

        public AuthServiceImpl(UserRepository userRepository,
                        ProfileRepository profileRepository,
                        PasswordEncoder passwordEncoder,
                        JwtService jwtService,
                        AuditService auditService) {
                this.userRepository = userRepository;
                this.profileRepository = profileRepository;
                this.passwordEncoder = passwordEncoder;
                this.jwtService = jwtService;
                this.auditService = auditService;
        }

        @Override
        public AuthResponseDTO login(LoginRequestDTO request) {

                String email = request.getEmail().toLowerCase();

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        throw new BusinessException("Invalid credentials");
                }

                if (!user.isActive()) {
                        throw new BusinessException("Account is deactivated. Contact your administrator.");
                }

                if (user.getStatus() != User.UserStatus.APPROVED) {
                        throw new BusinessException(
                                        "Account is not yet approved. Current status: " + user.getStatus());
                }

                String token = jwtService.generateToken(
                                user.getEmail(),
                                user.getRole().name(),
                                user.getId());

                LocalDateTime issuedAt = LocalDateTime.now();
                LocalDateTime expiresAt = issuedAt.plusSeconds(jwtService.getJwtExpirationSeconds());

                Profile profile = profileRepository.findByUser_Id(user.getId()).orElse(null);
                String fullName = profile != null ? profile.getFullName() : user.getEmail();

                auditService.log(
                                AuditLog.ActionType.LOGIN,
                                user,
                                "User",
                                user.getId(),
                                "User logged in");

                return new AuthResponseDTO(
                                token,
                                issuedAt,
                                expiresAt,
                                user.getId(),
                                user.getEmail(),
                                user.getRole().name(),
                                fullName);
        }

        @Override
        @Transactional
        public AuthResponseDTO register(RegisterRequestDTO request) {

                String email = request.getEmail().toLowerCase();

                if (userRepository.existsByEmail(email)) {
                        throw new BusinessException("Email already registered: " + email);
                }

                User user = new User();
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                user.setRole(User.Role.ADMIN);
                user.setStatus(User.UserStatus.APPROVED);
                user.setActive(true);

                user = userRepository.save(user);

                Profile profile = new Profile();
                profile.setUser(user);
                profile.setFullName(request.getFullName());

                profileRepository.save(profile);

                String token = jwtService.generateToken(
                                user.getEmail(),
                                user.getRole().name(),
                                user.getId());

                LocalDateTime issuedAt = LocalDateTime.now();
                LocalDateTime expiresAt = issuedAt.plusSeconds(jwtService.getJwtExpirationSeconds());

                auditService.log(
                                AuditLog.ActionType.REGISTER,
                                user,
                                "User",
                                user.getId(),
                                "Admin registered");

                return new AuthResponseDTO(
                                token,
                                issuedAt,
                                expiresAt,
                                user.getId(),
                                user.getEmail(),
                                user.getRole().name(),
                                request.getFullName());
        }
}