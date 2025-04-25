package com.medhir.rest.employee.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class EmployeeAttendanceDetailsDTO {
    private String name;
    private String employeeImgUrl;
    private LocalDate joiningDate;
    private List<String> weeklyOffs;
} 