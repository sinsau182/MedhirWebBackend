package com.medhir.rest.attendance.dto;

import lombok.Data;

@Data
public class FilteredAttendanceDTO {
    private String employeeId;
    private String employeeName;
    private String attendanceStatus;
    private String date;
    private String month;
    private String year;
} 