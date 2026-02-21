package com.sdl.service;

import com.sdl.dto.request.ProfileUpdateDTO;
import com.sdl.dto.response.ProfileResponseDTO;
import com.sdl.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {

    ProfileResponseDTO getProfile(User user);

    ProfileResponseDTO updateProfile(User user, ProfileUpdateDTO request);

    ProfileResponseDTO uploadProfilePicture(User user, MultipartFile file);
}