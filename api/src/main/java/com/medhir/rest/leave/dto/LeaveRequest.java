package com.medhir.rest.leave.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class LeaveRequest {
    private String employeeId;
    private String leaveName; // "Leave" or "Comp-Off"
    private String leaveType; // Only required for regular leave
    private LocalDate startDate;
    private LocalDate endDate;
    private String shiftType; // Will be converted to enum in the service layer
    private String reason;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime createdAt;
}