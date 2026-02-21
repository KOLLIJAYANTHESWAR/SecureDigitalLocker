package com.sdl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {

    private Long id;
    private String email;
    private String role;
    private String status;
    private boolean active;
    private String createdByEmail;
    private String approvedByEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}