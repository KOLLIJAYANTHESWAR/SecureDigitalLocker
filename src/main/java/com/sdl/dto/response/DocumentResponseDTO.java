package com.sdl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {

    private Long id;
    private String fileName;
    private String contentType;
    private Long fileSize;
    private String uploadedByEmail;
    private LocalDateTime uploadedAt;
    private List<String> visibleToRoles;
    private List<String> visibleToUsers;
}
