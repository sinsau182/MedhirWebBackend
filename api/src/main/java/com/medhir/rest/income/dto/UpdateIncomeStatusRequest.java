package com.medhir.rest.income.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateIncomeStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;
    
    private String remarks;
}
