package com.sdl.service;

import com.sdl.dto.request.ApprovalRequestDTO;
import com.sdl.dto.response.ApprovalResponseDTO;
import com.sdl.entity.User;

import java.util.List;

public interface ApprovalService {

    ApprovalResponseDTO processApproval(ApprovalRequestDTO request, User approver);

    List<ApprovalResponseDTO> getPendingApprovals(User requester);

    List<ApprovalResponseDTO> getApprovalHistory(User requester);
}