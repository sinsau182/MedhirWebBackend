package com.medhir.rest.leave.dto;

import com.medhir.rest.leave.model.Leave;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveWithEmployeeDetails extends Leave {
    private String employeeName;
    private String department;
}