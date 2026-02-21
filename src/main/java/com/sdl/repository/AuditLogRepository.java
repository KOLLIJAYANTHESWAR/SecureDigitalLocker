package com.sdl.repository;

import com.sdl.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

        @Query("""
                        SELECT al
                        FROM AuditLog al
                        JOIN FETCH al.user
                        WHERE al.user.id = :userId
                        ORDER BY al.timestamp DESC
                        """)
        List<AuditLog> findByUserIdOrderByTimestampDesc(
                        @Param("userId") Long userId);

        @Query("""
                        SELECT al
                        FROM AuditLog al
                        JOIN FETCH al.user
                        WHERE al.action = :action
                        ORDER BY al.timestamp DESC
                        """)
        List<AuditLog> findByActionOrderByTimestampDesc(
                        @Param("action") AuditLog.ActionType action);

        List<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(
                        String entityType,
                        Long entityId);

        @Query("""
                        SELECT al
                        FROM AuditLog al
                        JOIN FETCH al.user
                        ORDER BY al.timestamp DESC
                        """)
        List<AuditLog> findAllByOrderByTimestampDesc();
}