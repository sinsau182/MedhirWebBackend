package com.medhir.rest.leave.dto;

import com.medhir.rest.leave.model.LeaveModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LeaveWithEmployeeDetails extends LeaveModel {
    private String employeeName;
    private String department;
}