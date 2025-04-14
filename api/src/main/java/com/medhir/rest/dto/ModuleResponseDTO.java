package com.medhir.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponseDTO {
    private String moduleId;
    private String moduleName;
    private String description;
    private List<String> employeeNames;
    private String companyName;
} 