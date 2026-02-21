package com.sdl.service;

import com.sdl.dto.request.LoginRequestDTO;
import com.sdl.dto.request.RegisterRequestDTO;
import com.sdl.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO request);

    AuthResponseDTO register(RegisterRequestDTO request);
}
