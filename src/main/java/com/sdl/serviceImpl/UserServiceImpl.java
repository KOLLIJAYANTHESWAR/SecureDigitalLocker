package com.sdl.serviceImpl;

import com.sdl.dto.request.CreateUserRequestDTO;
import com.sdl.dto.response.UserResponseDTO;
import com.sdl.entity.ApprovalRequest;
import com.sdl.entity.AuditLog;
import com.sdl.entity.Profile;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.exception.BusinessException;
import com.sdl.exception.ResourceNotFoundException;
import com.sdl.repository.ApprovalRepository;
import com.sdl.repository.ProfileRepository;
import com.sdl.repository.UserRepository;
import com.sdl.service.AccessControlService;
import com.sdl.service.AuditService;
import com.sdl.service.UserService;
import com.sdl.util.MapperUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final ApprovalRepository approvalRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessControlService accessControlService;
    private final AuditService auditService;

    public UserServiceImpl(UserRepository userRepository,
            ProfileRepository profileRepository,
            ApprovalRepository approvalRepository,
            PasswordEncoder passwordEncoder,
            AccessControlService accessControlService,
            AuditService auditService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.approvalRepository = approvalRepository;
        this.passwordEncoder = passwordEncoder;
        this.accessControlService = accessControlService;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO request, User creator) {
        User.Role targetRole = User.Role.valueOf(request.getRole().name());

        if (!accessControlService.canCreateRole(creator, targetRole)) {
            throw new AccessDeniedException("You cannot create a user with role: " + targetRole);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists: " + request.getEmail());
        }

        User newUser = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(targetRole)
                .status(User.UserStatus.PENDING)
                .active(false)
                .createdBy(creator)
                .build();

        newUser = userRepository.save(newUser);

        Profile profile = Profile.builder()
                .user(newUser)
                .fullName(request.getFullName())
                .build();
        profileRepository.save(profile);

        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .requestedUser(newUser)
                .requestedBy(creator)
                .status(ApprovalRequest.ApprovalStatus.PENDING)
                .build();
        approvalRepository.save(approvalRequest);

        auditService.log(AuditLog.ActionType.USER_CREATED, creator, "User", newUser.getId(),
                "Created " + targetRole + " user: " + request.getEmail());

        return MapperUtil.toUserResponse(newUser);
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId, User performer) {
        User target = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!accessControlService.canDeactivateUser(performer, target)) {
            throw new AccessDeniedException("You cannot deactivate this user");
        }

        target.setActive(false);
        target.setStatus(User.UserStatus.REJECTED);
        userRepository.save(target);

        auditService.log(AuditLog.ActionType.LAYOFF, performer, "User", userId,
                "Deactivated user: " + target.getEmail());
    }

    @Override
    public UserResponseDTO getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return MapperUtil.toUserResponse(user);
    }

    @Override
    public List<UserResponseDTO> getUsersByRole(String role) {
        User.Role userRole = User.Role.valueOf(role.toUpperCase());
        return userRepository.findByRole(userRole).stream()
                .map(MapperUtil::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getActiveUsersByRole(String role) {
        User.Role userRole = User.Role.valueOf(role.toUpperCase());
        return userRepository.findByRoleAndActiveTrue(userRole).stream()
                .map(MapperUtil::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getUsersCreatedBy(Long creatorId) {
        return userRepository.findByCreatedBy_Id(creatorId).stream()
                .map(MapperUtil::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDTO> getPendingUsersByRole(String role) {
        User.Role userRole = User.Role.valueOf(role.toUpperCase());
        return userRepository.findByStatusAndRole(User.UserStatus.PENDING, userRole).stream()
                .map(MapperUtil::toUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}
