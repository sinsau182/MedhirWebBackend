package com.medhir.rest.dto;

import com.medhir.rest.employee.EmployeeModel;

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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public String getReportingManagerName() {
        return reportingManagerName;
    }

    public void setReportingManagerName(String reportingManagerName) {
        this.reportingManagerName = reportingManagerName;
    }
} 