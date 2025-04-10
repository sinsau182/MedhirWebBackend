package com.medhir.rest.leave.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "Leaves") // Store both Leaves and CompOff in the same collection
public class Leave {
    @Id
    @JsonIgnore
    private String id;
    private String leaveId; // New field for leave ID
    private String employeeId;
    private String employeeName;
    private String department;

    private String leaveName; // "Leave",  "Comp Off"
    private String leaveType; // "Casual Leave", "Medical Leave", "Comp Off"
    private LocalDate startDate;
    private LocalDate endDate;
    private String shiftType; // "Full Day", "First Half (Morning)", "Second Half (Afternoon)"
    private String reason;
    private String status = "Pending"; // Pending, Approved, Rejected
    private String remarks; // Used by the manager to provide remarks
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime createdAt;


}

