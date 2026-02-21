package com.sdl.repository;

import com.sdl.entity.ApprovalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApprovalRepository extends JpaRepository<ApprovalRequest, Long> {

    @Query("""
           SELECT ar 
           FROM ApprovalRequest ar
           JOIN FETCH ar.requestedUser ru
           LEFT JOIN FETCH ru.createdBy
           LEFT JOIN FETCH ru.approvedBy
           WHERE ar.status = :status
           ORDER BY ar.createdAt DESC
           """)
    List<ApprovalRequest> findByStatusWithDetails(
            @Param("status") ApprovalRequest.ApprovalStatus status);

    @Query("""
           SELECT ar 
           FROM ApprovalRequest ar
           JOIN FETCH ar.requestedUser ru
           LEFT JOIN FETCH ru.createdBy
           LEFT JOIN FETCH ru.approvedBy
           ORDER BY ar.createdAt DESC
           """)
    List<ApprovalRequest> findAllWithDetails();

    List<ApprovalRequest> findByRequestedByIdAndStatus(
            Long requestedById,
            ApprovalRequest.ApprovalStatus status);

    List<ApprovalRequest> findByRequestedById(Long requestedById);

    Optional<ApprovalRequest> findByRequestedUserId(Long requestedUserId);

    List<ApprovalRequest> findByRequestedByIdOrderByCreatedAtDesc(Long requestedById);
}