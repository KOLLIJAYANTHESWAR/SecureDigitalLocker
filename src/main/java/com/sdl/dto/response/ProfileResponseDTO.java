package com.sdl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponseDTO {

    private Long id;
    private Long userId;
    private String email;
    private String role;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String phone;

    // Cloud storage signed URL later
    private String profilePicturePath;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}