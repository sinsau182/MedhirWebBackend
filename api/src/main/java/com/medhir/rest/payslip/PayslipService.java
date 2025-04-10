package com.medhir.rest.payslip;

import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayslipService {

    @Autowired
    private EmployeeService employeeService;

    private static final Map<String, Integer> MONTH_MAP = new HashMap<>();
    static {
        MONTH_MAP.put("JANUARY", 1);
        MONTH_MAP.put("FEBRUARY", 2);
        MONTH_MAP.put("MARCH", 3);
        MONTH_MAP.put("APRIL", 4);
        MONTH_MAP.put("MAY", 5);
        MONTH_MAP.put("JUNE", 6);
        MONTH_MAP.put("JULY", 7);
        MONTH_MAP.put("AUGUST", 8);
        MONTH_MAP.put("SEPTEMBER", 9);
        MONTH_MAP.put("OCTOBER", 10);
        MONTH_MAP.put("NOVEMBER", 11);
        MONTH_MAP.put("DECEMBER", 12);
    }

    public PayslipModel generatePayslip(String employeeId, String month, int year) {
        // Get employee details
        System.out.println("Generating payslip for employee: " + employeeId);
        EmployeeModel employee = employeeService.getEmployeeById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found: " + employeeId));

        // Create payslip model
        PayslipModel payslip = new PayslipModel();
        
        // Set employee details
        payslip.setEmployeeId(employee.getEmployeeId());
        payslip.setEmployeeName(employee.getName());
        payslip.setDateOfJoining(employee.getJoiningDate());
        payslip.setDesignation(employee.getDesignation());
        payslip.setPan(employee.getIdProofs().getPanNo());
        payslip.setUanNumber(employee.getBankDetails().getAccountNumber()); // Assuming UAN is stored in account number for now

        // Format month with proper capitalization
        String formattedMonth = formatMonth(month);
        
        // Set payslip period
        payslip.setMonth(formattedMonth);
        payslip.setYear(year);
        payslip.setMonthYearDisplay(formattedMonth.substring(0, 3) + "-" + year);

        // Calculate days in month
        YearMonth yearMonth = YearMonth.of(year, getMonthNumber(month));
        payslip.setDaysInMonth(yearMonth.lengthOfMonth());

        // Set salary details from employee model
        payslip.setBasicSalaryPerMonth(employee.getSalaryDetails().getBasicSalary());
        payslip.setHraPerMonth(employee.getSalaryDetails().getHra());
        payslip.setOtherAllowancesPerMonth(employee.getSalaryDetails().getAllowances());
        payslip.setPfEmployeePerMonth(employee.getSalaryDetails().getEmployeePfContribution());
        payslip.setPfEmployerContributionPerMonth(employee.getSalaryDetails().getEmployerPfContribution());

        // Calculate this month's values (assuming full month for now)
        payslip.setBasicSalaryThisMonth(payslip.getBasicSalaryPerMonth());
        payslip.setHraThisMonth(payslip.getHraPerMonth());
        payslip.setOtherAllowancesThisMonth(payslip.getOtherAllowancesPerMonth());
        payslip.setPfEmployeeThisMonth(payslip.getPfEmployeePerMonth());

        // Calculate total earnings
        payslip.setTotalEarningsPerMonth(
            payslip.getBasicSalaryPerMonth() +
            payslip.getHraPerMonth() +
            payslip.getOtherAllowancesPerMonth()
        );
        payslip.setTotalEarningsThisMonth(
            payslip.getBasicSalaryThisMonth() +
            payslip.getHraThisMonth() +
            payslip.getOtherAllowancesThisMonth()
        );

        // Calculate deductions
        payslip.setPfEmployeeDeduction(payslip.getPfEmployeeThisMonth());
        payslip.setTotalDeductions(payslip.getPfEmployeeDeduction());

        // Calculate net pay
        payslip.setNetPay(payslip.getTotalEarningsThisMonth() - payslip.getTotalDeductions());

        return payslip;
    }

    private int getMonthNumber(String month) {
        String monthUpper = month.toUpperCase();
        Integer monthNumber = MONTH_MAP.get(monthUpper);
        if (monthNumber == null) {
            throw new IllegalArgumentException("Invalid month: " + month + ". Please use full month name (e.g., JANUARY, FEBRUARY, etc.)");
        }
        return monthNumber;
    }

    private String formatMonth(String month) {
        // Convert to lowercase first, then capitalize first letter
        return month.substring(0, 1).toUpperCase() + month.substring(1).toLowerCase();
    }
}
