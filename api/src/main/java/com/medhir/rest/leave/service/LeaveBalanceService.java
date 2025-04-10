package com.medhir.rest.leave.service;

import com.medhir.rest.employee.EmployeeService;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.leave.model.LeaveBalance;
import com.medhir.rest.leave.repositoris.LeaveBalanceRepository;
import com.medhir.rest.settings.department.DepartmentService;
import com.medhir.rest.settings.leaveSettings.leaveType.LeaveTypeService;
import com.medhir.rest.settings.leaveSettings.leavepolicy.LeavePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

@Service
public class LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private LeavePolicyService leavePolicyService;

    @Autowired
    private LeaveTypeService leaveTypeService;

    private double calculateMonthlyLeaves(String employeeId) {
        // Get employee's department
        var employeeOpt = employeeService.getEmployeeById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        var employee = employeeOpt.get();

        // Get department's leave policy
        var department = departmentService.getDepartmentById(employee.getDepartment());
        var leavePolicy = leavePolicyService.getLeavePolicyById(department.getLeavePolicy());
        var leaveTypeId = leavePolicy.getLeaveAllocations().get(0).getLeaveTypeId();
        var leaveType = leaveTypeService.getLeaveTypeById(leaveTypeId).getLeaveTypeName();

        // Find the leave allocation for this leave type
        var leaveAllocation = leavePolicy.getLeaveAllocations().stream()
                .filter(allocation -> {
                    var leaveTypeModel = leaveTypeService.getLeaveTypeById(allocation.getLeaveTypeId());
                    return leaveTypeModel.getLeaveTypeName().equals(leaveType);
                })
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Leave type " + leaveType + " not found in policy"));

        // Get the leave type details for accrual period
        var leaveTypeModel = leaveTypeService.getLeaveTypeById(leaveAllocation.getLeaveTypeId());
        double daysPerYear = leaveAllocation.getDaysPerYear();

        // Calculate monthly leaves based on accrual period
        switch (leaveTypeModel.getAccrualPeriod().toUpperCase()) {
            case "MONTHLY":
                return daysPerYear / 12.0;
            case "QUARTERLY":
                return daysPerYear / 4.0;
            case "ANNUALLY":
                return daysPerYear;
            default:
                throw new IllegalArgumentException("Invalid accrual period: " + leaveTypeModel.getAccrualPeriod());
        }
    }

    public LeaveBalance getOrCreateLeaveBalance(String employeeId, String month, int year) {
        // Validate employee exists
        var employeeOpt = employeeService.getEmployeeById(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }

        // Try to find existing balance
        Optional<LeaveBalance> existingBalance = leaveBalanceRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        if (existingBalance.isPresent()) {
            return existingBalance.get();
        }

        // Create new balance
        LeaveBalance newBalance = new LeaveBalance();
        newBalance.setEmployeeId(employeeId);
        newBalance.setMonth(month);
        newBalance.setNumericMonth(Month.valueOf(month.toUpperCase()).getValue());
        newBalance.setYear(year);

        // Get previous month's balance
        LeaveBalance previousBalance = getPreviousMonthBalance(employeeId, month, year);

        if (previousBalance != null) {
            // Set old balance from previous month's new balance
            newBalance.setOldLeaveBalance(previousBalance.getNewLeaveBalance());
            
            // Carry forward leaves from previous month
            newBalance.setAnnualLeavesCarryForwarded(previousBalance.getRemainingAnnualLeaves());
            newBalance.setCompOffLeavesCarryForwarded(previousBalance.getRemainingCompOffLeaves());
            
            // Carry forward yearly totals
            newBalance.setTotalAnnualLeavesEarnedSinceJanuary(previousBalance.getTotalAnnualLeavesEarnedSinceJanuary());
            newBalance.setTotalCompOffLeavesEarnedSinceJanuary(previousBalance.getTotalCompOffLeavesEarnedSinceJanuary());

            // Accumulate leaves taken this year
            newBalance.setLeavesTakenThisYear(previousBalance.getLeavesTakenThisYear() + previousBalance.getLeavesTakenInThisMonth());
        } else {
            newBalance.setOldLeaveBalance(0.0);
            newBalance.setAnnualLeavesCarryForwarded(0.0);
            newBalance.setCompOffLeavesCarryForwarded(0.0);
            newBalance.setTotalAnnualLeavesEarnedSinceJanuary(0.0);
            newBalance.setTotalCompOffLeavesEarnedSinceJanuary(0.0);
            newBalance.setLeavesTakenThisYear(0.0);
        }

        // Handle leaves carried from previous year
        if (Month.valueOf(month.toUpperCase()) == Month.JANUARY) {
            // For January, get December of previous year
            Optional<LeaveBalance> decemberBalance = leaveBalanceRepository.findByEmployeeIdAndNumericMonthAndYear(
                employeeId, 
                12,  // December
                year - 1  // Previous year
            );
            
            if (decemberBalance.isPresent()) {
                newBalance.setLeavesCarriedFromPreviousYear(decemberBalance.get().getRemainingAnnualLeaves());
            } else {
                newBalance.setLeavesCarriedFromPreviousYear(0.0);
            }
        } else {
            // For other months, carry forward from previous month
            newBalance.setLeavesCarriedFromPreviousYear(previousBalance != null ? 
                previousBalance.getLeavesCarriedFromPreviousYear() : 0.0);
        }

        // Calculate earned leaves based on policy
        double earnedLeaves = calculateMonthlyLeaves(employeeId);
        newBalance.setAnnualLeavesEarned(earnedLeaves);
        newBalance.setCompOffLeavesEarned(0.0);
        newBalance.setLeavesTakenInThisMonth(0.0);

        // Update yearly totals
        newBalance.setTotalAnnualLeavesEarnedSinceJanuary(
            newBalance.getTotalAnnualLeavesEarnedSinceJanuary() + earnedLeaves
        );

        // Calculate remaining leaves
        updateRemainingLeaves(newBalance);

        return leaveBalanceRepository.save(newBalance);
    }

    private LeaveBalance getPreviousMonthBalance(String employeeId, String currentMonth, int year) {
        Month month = Month.valueOf(currentMonth.toUpperCase());
        int previousMonthValue = month.getValue() - 1;
        int previousYear = year;

        if (previousMonthValue == 0) {
            previousMonthValue = 12;
            previousYear--;
        }

        return leaveBalanceRepository.findByEmployeeIdAndNumericMonthAndYear(employeeId, previousMonthValue, previousYear)
                .orElse(null);
    }

    private void updateRemainingLeaves(LeaveBalance balance) {
        // Calculate remaining annual leaves (can be negative)
        balance.setRemainingAnnualLeaves(
            balance.getAnnualLeavesCarryForwarded() +
            balance.getAnnualLeavesEarned() -
            balance.getLeavesTakenInThisMonth()
        );

        // Calculate remaining comp-off leaves (should not be negative)
        balance.setRemainingCompOffLeaves(
            Math.max(0, balance.getCompOffLeavesCarryForwarded() +
            balance.getCompOffLeavesEarned())
        );

        // Calculate new balance (can be negative if annual leaves are negative)
        balance.setNewLeaveBalance(
            balance.getRemainingAnnualLeaves() +
            balance.getRemainingCompOffLeaves()
        );
    }

    public LeaveBalance getCurrentMonthBalance(String employeeId) {
        LocalDate now = LocalDate.now();
        return getOrCreateLeaveBalance(employeeId, now.getMonth().toString(), now.getYear());
    }

    public void updateLeavesTaken(String employeeId, double days) {
        LeaveBalance balance = getCurrentMonthBalance(employeeId);
        balance.setLeavesTakenInThisMonth(balance.getLeavesTakenInThisMonth() + days);
        balance.setLeavesTakenThisYear(balance.getLeavesTakenThisYear() + days);
        updateRemainingLeaves(balance);
        leaveBalanceRepository.save(balance);
    }

    public void updateCompOffLeavesTaken(String employeeId, double days) {
        LeaveBalance balance = getCurrentMonthBalance(employeeId);
        // Reduce comp-off leaves earned by the days taken
        // balance.setCompOffLeavesEarned(balance.getCompOffLeavesEarned() - days);
        balance.setRemainingCompOffLeaves(balance.getRemainingCompOffLeaves() - days);
        updateRemainingLeaves(balance);
        leaveBalanceRepository.save(balance);
    }

    public void addCompOffEarned(String employeeId, double days) {
        LeaveBalance balance = getCurrentMonthBalance(employeeId);
        // Add to comp-off earned fields
        balance.setCompOffLeavesEarned(balance.getCompOffLeavesEarned() + days);
        balance.setRemainingCompOffLeaves(balance.getRemainingCompOffLeaves() + days);
        // Update yearly total for comp-off
        balance.setTotalCompOffLeavesEarnedSinceJanuary(
            balance.getTotalCompOffLeavesEarnedSinceJanuary() + days
        );
        updateRemainingLeaves(balance);
        leaveBalanceRepository.save(balance);
    }
} 