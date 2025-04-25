package com.medhir.rest.payslip;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PayslipModel {

    

    // Payslip Period Display
    private String monthYearDisplay; // Format: "Feb-2025"

    // Employee Details
    private String employeeId;
    private String employeeName;
    private LocalDate dateOfJoining;
    private String designation;
    private String pan;
    private String uanNumber;

    // Leave Details
    private int daysInMonth;
    private int salaryPaidForDays;
    private int leavesTaken;
    private int lossOfPayDays;
    private double compOffLeavesEarned;
    private double annualLeavesEarned;
    private double oldLeavesBalance;
    private double newLeavesBalance;

    // Monthly Earnings
    private double basicSalaryPerMonth;
    private double basicSalaryThisMonth;
    private double hraPerMonth;
    private double hraThisMonth;
    private double pfEmployerContributionPerMonth;
    private double pfEmployerContributionThisMonth;
    private double pfEmployeePerMonth;
    private double pfEmployeeThisMonth;
    private double fuelAllowancesPerMonth;
    private double fuelAllowancesThisMonth;
    private double otherAllowancesPerMonth;
    private double otherAllowancesThisMonth;
    private double arrearsPaidPerMonth;
    private double arrearsPaidThisMonth;
    private double totalEarningsPerMonth;
    private double totalEarningsThisMonth;

    // Deductions
    private double pfEmployerContributionDeduction;
    private double pfEmployeeDeduction;
    private double professionalTax;
    private double tds;
    private double advanceAdjusted;
    private double arrearsDeducted;
    private double totalDeductions;
    private double salaryAdvanceBalance;

    // Final Amount
    private double netPay;

    // Payslip Period
    private String month;
    private int year;

   
}



