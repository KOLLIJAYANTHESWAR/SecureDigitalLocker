package com.sdl.service;

import com.sdl.dto.response.AuditLogResponseDTO;
import com.sdl.entity.AuditLog;
import com.sdl.entity.User;

import java.util.List;

public interface AuditService {

    void log(AuditLog.ActionType action,
             User performedBy,
             String targetEntity,
             Long targetId,
             String details);

    List<AuditLogResponseDTO> getLogsByUser(Long userId);

    List<AuditLogResponseDTO> getLogsByAction(AuditLog.ActionType action);

    List<AuditLogResponseDTO> getAllLogs();
}