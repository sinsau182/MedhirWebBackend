package com.medhir.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponseDTO {
    private String moduleId;
    private String moduleName;
    private String description;
    private List<Map<String, String>> employees; // List of maps containing employeeId and name
    private Map<String, String> company; // Map containing companyId and name
} 