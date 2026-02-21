package com.sdl.controller;

import com.sdl.dto.request.ApprovalRequestDTO;
import com.sdl.dto.response.ApiResponseDTO;
import com.sdl.dto.response.ApprovalResponseDTO;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.service.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    // Process approval (approve or reject)
    @PostMapping("/process")
    public ResponseEntity<ApiResponseDTO<ApprovalResponseDTO>> processApproval(
            @Valid @RequestBody ApprovalRequestDTO request,
            @AuthenticationPrincipal User approver) {

        if (approver == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        ApprovalResponseDTO response =
                approvalService.processApproval(request, approver);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Approval processed successfully", response)
        );
    }

    // Get pending approvals for current user
    @GetMapping("/pending")
    public ResponseEntity<ApiResponseDTO<List<ApprovalResponseDTO>>> getPendingApprovals(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        List<ApprovalResponseDTO> approvals =
                approvalService.getPendingApprovals(user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Pending approvals fetched", approvals)
        );
    }

    // Get approval history
    @GetMapping("/history")
    public ResponseEntity<ApiResponseDTO<List<ApprovalResponseDTO>>> getApprovalHistory(
            @AuthenticationPrincipal User user) {

        if (user == null) {
            throw new AccessDeniedException("Unauthorized");
        }

        List<ApprovalResponseDTO> approvals =
                approvalService.getApprovalHistory(user);

        return ResponseEntity.ok(
                ApiResponseDTO.success("Approval history fetched", approvals)
        );
    }
}