package com.medhir.rest.employee.dto;

import com.medhir.rest.employee.EmployeeModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeWithLeaveDetailsDTO extends EmployeeModel {
    private String leavePolicyName;
    private List<String> leaveTypeNames;
    private List<String> leaveTypeIds;
    private String departmentName;
    private String designationName;

    public EmployeeWithLeaveDetailsDTO(EmployeeModel employee) {
        BeanUtils.copyProperties(employee, this);
    }
} 