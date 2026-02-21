package com.sdl.util;

import com.sdl.dto.response.*;
import com.sdl.entity.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class MapperUtil {

    private MapperUtil() {
    }

    public static UserResponseDTO toUserResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        dto.setActive(user.isActive());
        dto.setCreatedByEmail(
                user.getCreatedBy() != null ? user.getCreatedBy().getEmail() : null);
        dto.setApprovedByEmail(
                user.getApprovedBy() != null ? user.getApprovedBy().getEmail() : null);
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }

    public static ProfileResponseDTO toProfileResponse(Profile profile) {
        if (profile == null) {
            return null;
        }

        User user = profile.getUser();

        ProfileResponseDTO dto = new ProfileResponseDTO();
        dto.setId(profile.getId());
        dto.setUserId(user != null ? user.getId() : null);
        dto.setEmail(user != null ? user.getEmail() : null);
        dto.setFullName(profile.getFullName());
        dto.setDob(profile.getDob());
        dto.setGender(profile.getGender());
        dto.setProfilePicturePath(profile.getProfilePicturePath());
        dto.setRole(user != null && user.getRole() != null
                ? user.getRole().name()
                : null);

        return dto;
    }

    public static DocumentResponseDTO toDocumentResponse(
            Document document,
            List<DocumentVisibility> visibilities) {

        if (document == null) {
            return null;
        }

        List<String> roles = Collections.emptyList();
        List<String> users = Collections.emptyList();

        if (visibilities != null && !visibilities.isEmpty()) {
            roles = visibilities.stream()
                    .filter(v -> v != null && v.getVisibleToRole() != null)
                    .map(v -> v.getVisibleToRole().name())
                    .distinct()
                    .collect(Collectors.toList());

            users = visibilities.stream()
                    .filter(v -> v != null && v.getVisibleToUser() != null)
                    .map(v -> v.getVisibleToUser().getEmail())
                    .distinct()
                    .collect(Collectors.toList());
        }

        DocumentResponseDTO dto = new DocumentResponseDTO();
        dto.setId(document.getId());
        dto.setFileName(document.getFileName());
        dto.setContentType(document.getContentType());
        dto.setFileSize(document.getFileSize());
        dto.setUploadedByEmail(
                document.getUploadedBy() != null
                        ? document.getUploadedBy().getEmail()
                        : null);
        dto.setUploadedAt(document.getUploadedAt());
        dto.setVisibleToRoles(roles);
        dto.setVisibleToUsers(users);

        return dto;
    }

    public static ApprovalResponseDTO toApprovalResponse(ApprovalRequest request) {
        if (request == null) {
            return null;
        }

        ApprovalResponseDTO dto = new ApprovalResponseDTO();
        dto.setId(request.getId());
        dto.setRequestedUser(toUserResponse(request.getRequestedUser()));
        dto.setRequestedByEmail(
                request.getRequestedBy() != null
                        ? request.getRequestedBy().getEmail()
                        : null);
        dto.setApprovedByEmail(
                request.getApprovedBy() != null
                        ? request.getApprovedBy().getEmail()
                        : null);
        dto.setStatus(request.getStatus() != null ? request.getStatus().name() : null);
        dto.setRemarks(request.getRemarks());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());

        return dto;
    }

    public static AuditLogResponseDTO toAuditLogResponse(AuditLog log) {
        if (log == null) {
            return null;
        }

        AuditLogResponseDTO dto = new AuditLogResponseDTO();
        dto.setId(log.getId());
        dto.setAction(log.getAction() != null ? log.getAction().name() : null);
        dto.setPerformedByEmail(
                log.getUser() != null
                        ? log.getUser().getEmail()
                        : null);
        dto.setTargetEntity(log.getEntityType());
        dto.setTargetId(log.getEntityId());
        dto.setDetails(log.getDetails());
        dto.setTimestamp(log.getTimestamp());

        return dto;
    }
}