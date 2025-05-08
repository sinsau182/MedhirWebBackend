package com.medhir.rest.reimbursements;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "Reimbursements")
public class ReimbursementModel {
    @Id
    private String id;
    private String employeeId;
    private String employeeName;
    private String department;
    private String reimbursementType; // Project or Non-Project
    private Double amount;
    private String category;
    private String description;
    private String receiptUrl;
    private String status; // Pending, Approved, etc.
    private LocalDateTime createdAt;
}