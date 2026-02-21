package com.sdl.serviceImpl;

import com.sdl.dto.response.AuditLogResponseDTO;
import com.sdl.entity.AuditLog;
import com.sdl.entity.User;
import com.sdl.repository.AuditLogRepository;
import com.sdl.service.AuditService;
import com.sdl.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void log(AuditLog.ActionType action,
            User performedBy,
            String targetEntity,
            Long targetId,
            String details) {

        if (performedBy == null) {
            return;
        }

        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .user(performedBy)
                .entityType(targetEntity)
                .entityId(targetId)
                .details(details)
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getLogsByUser(Long userId) {
        return auditLogRepository
                .findByUserIdOrderByTimestampDesc(userId)
                .stream()
                .map(MapperUtil::toAuditLogResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getLogsByAction(AuditLog.ActionType action) {
        return auditLogRepository
                .findByActionOrderByTimestampDesc(action)
                .stream()
                .map(MapperUtil::toAuditLogResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponseDTO> getAllLogs() {
        return auditLogRepository
                .findAllByOrderByTimestampDesc()
                .stream()
                .map(MapperUtil::toAuditLogResponse)
                .collect(Collectors.toList());
    }
}