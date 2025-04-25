package com.medhir.rest.leave.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "Leaves") // Store both Leaves and CompOff in the same collection
public class LeaveModel {
    @Id
    @JsonIgnore
    private String id;
    private String leaveId; // New field for leave ID
    private String employeeId;
    @NotBlank(message = "Company id cannot be empty")
    private String companyId; // Added companyId field
    private String leaveName; // "Leave",  "Comp Off"
    private String leaveType; // "Casual Leave", "Medical Leave", "Comp Off"
    private LocalDate startDate;
    private LocalDate endDate;
    private String shiftType; // Will be converted to enum in the service layer
    private String reason;
    private String status = "Pending"; // Pending, Approved, Rejected
    private String remarks; // Used by the manager to provide remarks
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime createdAt;
}