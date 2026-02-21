package com.sdl.serviceImpl;

import com.sdl.dto.request.ApprovalRequestDTO;
import com.sdl.dto.response.ApprovalResponseDTO;
import com.sdl.entity.ApprovalRequest;
import com.sdl.entity.AuditLog;
import com.sdl.entity.User;
import com.sdl.exception.AccessDeniedException;
import com.sdl.exception.BusinessException;
import com.sdl.exception.ResourceNotFoundException;
import com.sdl.repository.ApprovalRepository;
import com.sdl.repository.UserRepository;
import com.sdl.service.AccessControlService;
import com.sdl.service.ApprovalService;
import com.sdl.service.AuditService;
import com.sdl.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final AccessControlService accessControlService;
    private final AuditService auditService;

    public ApprovalServiceImpl(ApprovalRepository approvalRepository,
                               UserRepository userRepository,
                               AccessControlService accessControlService,
                               AuditService auditService) {
        this.approvalRepository = approvalRepository;
        this.userRepository = userRepository;
        this.accessControlService = accessControlService;
        this.auditService = auditService;
    }

    @Override
    public ApprovalResponseDTO processApproval(ApprovalRequestDTO request, User approver) {

        if (approver == null) {
            throw new AccessDeniedException("Approver not authenticated");
        }

        ApprovalRequest approval = approvalRepository.findById(request.getApprovalRequestId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "ApprovalRequest",
                                request.getApprovalRequestId()
                        )
                );

        if (approval.getStatus() != ApprovalRequest.ApprovalStatus.PENDING) {
            throw new BusinessException("This approval request has already been processed");
        }

        User targetUser = approval.getRequestedUser();

        if (targetUser == null) {
            throw new BusinessException("Invalid approval request: Target user not found");
        }

        if (!accessControlService.canApproveRole(approver, targetUser.getRole())) {
            throw new AccessDeniedException(
                    "You cannot approve users with role: " + targetUser.getRole()
            );
        }

        approval.setApprovedBy(approver);
        approval.setRemarks(request.getRemarks());

        if (request.getAction() == ApprovalRequestDTO.ApprovalAction.APPROVE) {

            approval.setStatus(ApprovalRequest.ApprovalStatus.APPROVED);

            targetUser.setStatus(User.UserStatus.APPROVED);
            targetUser.setActive(true);
            targetUser.setApprovedBy(approver);

            auditService.log(
                    AuditLog.ActionType.APPROVAL,
                    approver,
                    "User",
                    targetUser.getId(),
                    "Approved user: " + targetUser.getEmail()
            );

        } else {

            approval.setStatus(ApprovalRequest.ApprovalStatus.REJECTED);

            targetUser.setStatus(User.UserStatus.REJECTED);
            targetUser.setActive(false);

            auditService.log(
                    AuditLog.ActionType.REJECTION,
                    approver,
                    "User",
                    targetUser.getId(),
                    "Rejected user: " + targetUser.getEmail()
                            + (request.getRemarks() != null
                            ? " | Remarks: " + request.getRemarks()
                            : "")
            );
        }

        userRepository.save(targetUser);
        approvalRepository.save(approval);

        return MapperUtil.toApprovalResponse(approval);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalResponseDTO> getPendingApprovals(User requester) {

        if (requester == null) {
            throw new AccessDeniedException("Requester not authenticated");
        }

        return approvalRepository
                .findByStatusWithDetails(ApprovalRequest.ApprovalStatus.PENDING)
                .stream()
                .filter(req ->
                        accessControlService.canApproveRole(
                                requester,
                                req.getRequestedUser().getRole()
                        )
                )
                .map(MapperUtil::toApprovalResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalResponseDTO> getApprovalHistory(User requester) {

        if (requester == null) {
            throw new AccessDeniedException("Requester not authenticated");
        }

        return approvalRepository
                .findAllWithDetails()
                .stream()
                .filter(req -> req.getStatus() != ApprovalRequest.ApprovalStatus.PENDING)
                .filter(req ->
                        (req.getRequestedBy() != null &&
                                req.getRequestedBy().getId().equals(requester.getId()))
                                ||
                        (req.getApprovedBy() != null &&
                                req.getApprovedBy().getId().equals(requester.getId()))
                )
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(MapperUtil::toApprovalResponse)
                .toList();
    }
}