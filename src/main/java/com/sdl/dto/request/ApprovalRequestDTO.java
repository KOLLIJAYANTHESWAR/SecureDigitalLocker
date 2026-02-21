package com.sdl.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ApprovalRequestDTO {

    public enum ApprovalAction {
        APPROVE,
        REJECT
    }

    @NotNull(message = "Approval request ID is required")
    private Long approvalRequestId;

    @NotNull(message = "Approval action is required")
    private ApprovalAction action;

    @Size(max = 500, message = "Remarks must not exceed 500 characters")
    private String remarks;

    public ApprovalRequestDTO() {
    }

    public ApprovalRequestDTO(Long approvalRequestId, ApprovalAction action, String remarks) {
        this.approvalRequestId = approvalRequestId;
        this.action = action;
        this.remarks = remarks;
    }

    // Getters & Setters

    public Long getApprovalRequestId() {
        return approvalRequestId;
    }

    public void setApprovalRequestId(Long approvalRequestId) {
        this.approvalRequestId = approvalRequestId;
    }

    public ApprovalAction getAction() {
        return action;
    }

    public void setAction(ApprovalAction action) {
        this.action = action;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}