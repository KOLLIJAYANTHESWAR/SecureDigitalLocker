package com.sdl.repository;

import com.sdl.entity.DocumentVisibility;
import com.sdl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DocumentVisibilityRepository extends JpaRepository<DocumentVisibility, Long> {

    // Explicit nested property reference
    List<DocumentVisibility> findByDocument_Id(Long documentId);

    void deleteByDocument_Id(Long documentId);

    List<DocumentVisibility> findByVisibleToUser_Id(Long userId);

    List<DocumentVisibility> findByVisibleToRole(User.Role role);

    List<DocumentVisibility> findByVisibleToUser_IdOrVisibleToRole(Long userId, User.Role role);

    @Query("""
            SELECT dv
            FROM DocumentVisibility dv
            WHERE dv.document.id = :documentId
            AND (dv.visibleToUser.id = :userId
                 OR dv.visibleToRole = :role)
            """)
    List<DocumentVisibility> findAccessForUser(
            @Param("documentId") Long documentId,
            @Param("userId") Long userId,
            @Param("role") User.Role role);

    boolean existsByDocument_IdAndVisibleToUser_Id(Long documentId, Long userId);

    boolean existsByDocument_IdAndVisibleToRole(Long documentId, User.Role role);
}