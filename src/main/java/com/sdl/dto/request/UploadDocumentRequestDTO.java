package com.sdl.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public class UploadDocumentRequestDTO {

    // Optional: specific users
    private List<Long> visibleToUserIds;

    // Optional: roles (ADMIN, HR, MANAGER, USER)
    private List<String> visibleToRoles;

    public UploadDocumentRequestDTO() {
    }

    public UploadDocumentRequestDTO(List<Long> visibleToUserIds,
                                    List<String> visibleToRoles) {
        this.visibleToUserIds = visibleToUserIds;
        this.visibleToRoles = visibleToRoles;
    }

    public List<Long> getVisibleToUserIds() {
        return visibleToUserIds;
    }

    public void setVisibleToUserIds(List<Long> visibleToUserIds) {
        this.visibleToUserIds = visibleToUserIds;
    }

    public List<String> getVisibleToRoles() {
        return visibleToRoles;
    }

    public void setVisibleToRoles(List<String> visibleToRoles) {
        this.visibleToRoles = visibleToRoles;
    }
}