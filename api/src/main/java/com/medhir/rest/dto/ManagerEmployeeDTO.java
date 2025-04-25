package com.medhir.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManagerEmployeeDTO {
    private String employeeId;
    private String name;
    private String fathersName;
    private String phone;
    private String emailOfficial;
    private LocalDate joiningDate;
    private String designationName;
    private String currentAddress;
} 