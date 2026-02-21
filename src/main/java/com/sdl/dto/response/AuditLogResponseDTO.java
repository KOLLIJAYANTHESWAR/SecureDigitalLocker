package com.sdl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long id;
    private String action;
    private String performedByEmail;
    private String targetEntity;
    private Long targetId;
    private String details;
    private String ipAddress;
    private LocalDateTime timestamp;
}