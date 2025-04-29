package com.medhir.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyDesignationDTO {
    private String designationId;
    private String name;
    private String department;
    private String description;
    private boolean isManager;
    private boolean overtimeEligible;
} 