package com.medhir.rest.settings.leaveSettings.leavepolicy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "leave_policies")
public class LeavePolicyModel {

    @Id
    @JsonIgnore
    private String id;

    private String leavePolicyId;

    @NotBlank(message = "Policy name is required")
    private String name;

    @NotEmpty(message = "Leave allocations are required")
    @Valid
    private List<LeaveAllocation> leaveAllocations;

    private String createdAt;
    private String updatedAt;
} 