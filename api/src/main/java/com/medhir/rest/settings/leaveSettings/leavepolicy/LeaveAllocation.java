package com.medhir.rest.settings.leaveSettings.leavepolicy;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveAllocation {
    @NotBlank(message = "Leave type ID is required")
    private String leaveTypeId;

    @NotNull(message = "Days per year is required")
    private Integer daysPerYear;
} 