package com.medhir.rest.dto;

import com.medhir.rest.employee.EmployeeModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailsDTO extends EmployeeModel {
    private String departmentName;
    private String designationName;
    private String reportingManagerName;

    public EmployeeDetailsDTO(EmployeeModel employee) {
        // Copy all fields from EmployeeModel
        this.setEmployeeId(employee.getEmployeeId());
        this.setName(employee.getName());
        this.setCompanyId(employee.getCompanyId());
        this.setDepartment(employee.getDepartment());
        this.setDesignation(employee.getDesignation());
        this.setReportingManager(employee.getReportingManager());
        this.setEmailPersonal(employee.getEmailPersonal());
        this.setEmailOfficial(employee.getEmailOfficial());
        this.setPhone(employee.getPhone());
        this.setAlternatePhone(employee.getAlternatePhone());
        this.setGender(employee.getGender());
        this.setPermanentAddress(employee.getPermanentAddress());
        this.setCurrentAddress(employee.getCurrentAddress());
        this.setFathersName(employee.getFathersName());
        this.setEmployeeImgUrl(employee.getEmployeeImgUrl());
        this.setJoiningDate(employee.getJoiningDate());
        this.setOvertimeEligibile(employee.isOvertimeEligibile());
        this.setPfEnrolled(employee.isPfEnrolled());
        this.setUanNumber(employee.getUanNumber());
        this.setEsicEnrolled(employee.isEsicEnrolled());
        this.setEsicNumber(employee.getEsicNumber());
        this.setWeeklyOffs(employee.getWeeklyOffs());
        this.setRoles(employee.getRoles());
        this.setModuleIds(employee.getModuleIds());
        this.setIdProofs(employee.getIdProofs());
        this.setBankDetails(employee.getBankDetails());
        this.setSalaryDetails(employee.getSalaryDetails());
    }
} 