package com.medhir.rest.leave.dto;

import lombok.Data;

@Data
public class UpdateLeaveStatusRequest {
    private String leaveId;
    private String status; // "Approved" or "Rejected"
    private String remarks;
} 