package com.sdl.serviceImpl;

import com.sdl.dto.request.ProfileUpdateDTO;
import com.sdl.dto.response.ProfileResponseDTO;
import com.sdl.entity.AuditLog;
import com.sdl.entity.Profile;
import com.sdl.entity.User;
import com.sdl.exception.ResourceNotFoundException;
import com.sdl.repository.ProfileRepository;
import com.sdl.service.AuditService;
import com.sdl.service.ProfileService;
import com.sdl.service.StorageService;
import com.sdl.util.FileValidator;
import com.sdl.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final StorageService storageService;
    private final AuditService auditService;

    public ProfileServiceImpl(ProfileRepository profileRepository,
            StorageService storageService,
            AuditService auditService) {
        this.profileRepository = profileRepository;
        this.storageService = storageService;
        this.auditService = auditService;
    }

    @Override
    public ProfileResponseDTO getProfile(User user) {
        Profile profile = profileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", user.getId().toString()));
        return MapperUtil.toProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(User user, ProfileUpdateDTO request) {
        Profile profile = profileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", user.getId().toString()));

        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getDob() != null) {
            profile.setDob(request.getDob());
        }
        if (request.getGender() != null) {
            profile.setGender(request.getGender());
        }

        profile = profileRepository.save(profile);

        auditService.log(AuditLog.ActionType.PROFILE_UPDATE, user, "Profile", profile.getId(),
                "Profile updated");

        return MapperUtil.toProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDTO uploadProfilePicture(User user, MultipartFile file) {
        FileValidator.validateImage(file);

        Profile profile = profileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile", "userId", user.getId().toString()));

        // Delete old picture if exists
        if (profile.getProfilePicturePath() != null) {
            try {
                storageService.delete(profile.getProfilePicturePath());
            } catch (Exception ignored) {
                // Old file may not exist
            }
        }

        String storagePath = storageService.upload(file, "profiles");
        profile.setProfilePicturePath(storagePath);
        profile = profileRepository.save(profile);

        auditService.log(AuditLog.ActionType.PROFILE_UPDATE, user, "Profile", profile.getId(),
                "Profile picture uploaded");

        return MapperUtil.toProfileResponse(profile);
    }
}
