package com.medhir.rest.leave.service;

import com.medhir.rest.employee.EmployeeService;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.leave.dto.LeaveRequest;
import com.medhir.rest.leave.dto.UpdateLeaveStatusRequest;
import com.medhir.rest.leave.model.Leave;
import com.medhir.rest.leave.model.LeaveBalance;
import com.medhir.rest.leave.repositoris.LeaveRepository;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class LeaveApplicationService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private GeneratedId generatedId;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${leave.service.url}")
    private String APPLY_LEAVE_URL;

    public Leave applyLeave(LeaveRequest request) {
        // Validate employee exists and get their details
        Optional<com.medhir.rest.employee.EmployeeModel> employeeOpt = employeeService.getEmployeeById(request.getEmployeeId());
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId());
        }

        com.medhir.rest.employee.EmployeeModel employee = employeeOpt.get();

        // Set end date equal to start date if not provided
        if (request.getEndDate() == null) {
            request.setEndDate(request.getStartDate());
        }

        // Create leave record
        Leave leave = new Leave();
        leave.setLeaveId(generatedId.generateId("LID", Leave.class, "leaveId"));
        leave.setEmployeeId(request.getEmployeeId());
        leave.setEmployeeName(employee.getName());
        leave.setDepartment(employee.getDepartment());
        leave.setLeaveName(request.getLeaveName());
        leave.setLeaveType(request.getLeaveType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setShiftType(request.getShiftType());
        leave.setReason(request.getReason());
        leave.setStatus("Pending");

        // Check balance and add warning if insufficient (only for regular leaves)
        if ("Leave".equals(request.getLeaveName())) {
            checkAndAddWarning(leave);
        }

        return leaveRepository.save(leave);
    }

    private void checkAndAddWarning(Leave leave) {
        double requestedDays = calculateLeaveDays(leave);
        LeaveBalance currentBalance = leaveBalanceService.getCurrentMonthBalance(leave.getEmployeeId());
        
        // First check comp-off balance
        double compOffBalance = currentBalance.getRemainingCompOffLeaves();
        double annualBalance = currentBalance.getRemainingAnnualLeaves();
        double totalAvailable = compOffBalance + annualBalance;

        if (requestedDays > totalAvailable) {
            leave.setRemarks("WARNING: Insufficient leave balance. This will be marked as LOP if approved. " +
                           String.format("Requested: %.1f days, Available: %.1f days (Comp-off: %.1f, Annual: %.1f)",
                           requestedDays, totalAvailable, compOffBalance, annualBalance));
        }
    }

    public Leave updateLeaveStatus(UpdateLeaveStatusRequest request) {
        Optional<Leave> leaveOpt = leaveRepository.findByLeaveId(request.getLeaveId());
        if (leaveOpt.isEmpty()) {
            throw new ResourceNotFoundException("Leave not found with ID: " + request.getLeaveId());
        }

        Leave leave = leaveOpt.get();

        
        if (!"Approved".equals(request.getStatus()) && !"Rejected".equals(request.getStatus())) {
            throw new IllegalArgumentException("Status must be either 'Approved' or 'Rejected'");
        }

        leave.setStatus(request.getStatus());
        leave.setRemarks(request.getRemarks());

        if ("Approved".equals(request.getStatus())) {
            if ("Leave".equals(leave.getLeaveName())) {
                handleRegularLeaveApproval(leave);
            } else if ("Comp-Off".equals(leave.getLeaveName())) {
                // For comp-off, only add to earned balance without reducing any balances
                double days = calculateLeaveDays(leave);
                leaveBalanceService.addCompOffEarned(leave.getEmployeeId(), days);
            }
        }

        return leaveRepository.save(leave);
    }

    private void handleRegularLeaveApproval(Leave leave) {
        double requestedDays = calculateLeaveDays(leave);
        LeaveBalance currentBalance = leaveBalanceService.getCurrentMonthBalance(leave.getEmployeeId());
        
        double compOffBalance = currentBalance.getRemainingCompOffLeaves();
        double annualBalance = currentBalance.getRemainingAnnualLeaves();
        double totalAvailable = compOffBalance + annualBalance;

        // First use comp-off balance if available
        if (compOffBalance > 0) {
            leaveBalanceService.updateCompOffLeavesTaken(leave.getEmployeeId(), compOffBalance);
            requestedDays -= compOffBalance;
        }

        // Then use annual leave balance (can go negative)
        if (requestedDays > 0) {
            leaveBalanceService.updateLeavesTaken(leave.getEmployeeId(), requestedDays);
        }

        // Calculate how many days we can mark as present with approved leave
        double daysWithLeave = Math.min(totalAvailable, requestedDays + compOffBalance);
        double daysAsLOP = Math.max(0, requestedDays - totalAvailable);

        if (daysWithLeave > 0) {
            // Calculate dates for days with leave
            LocalDate leaveStartDate = leave.getStartDate();
            LocalDate leaveEndDate = leaveStartDate.plusDays((long)daysWithLeave - 1);

            // Mark days with available leave as present with approved leave
            markPresentWithApprovedLeaveInAttendance(
                leave.getEmployeeId(),
                leave.getLeaveType(),
                leaveStartDate,
                leaveEndDate,
                leave.getReason()
            );

            // If there are days to mark as LOP
            if (daysAsLOP > 0) {
                LocalDate lopStartDate = leaveEndDate.plusDays(1);
                markApprovedLOPInAttendance(
                    leave.getEmployeeId(),
                    leave.getLeaveType(),
                    lopStartDate,
                    leave.getEndDate(),
                    leave.getReason()
                );
            }
        } else {
            // All days are LOP
            markApprovedLOPInAttendance(
                leave.getEmployeeId(),
                leave.getLeaveType(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getReason()
            );
        }
    }

    private double calculateLeaveDays(Leave leave) {
        long totalDays = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate()) + 1;
        
        switch (leave.getShiftType()) {
            case "Full Day":
                return totalDays;
            case "First Half (Morning)":
            case "Second Half (Evening)":
                return totalDays * 0.5;
            default:
                return totalDays;
        }
    }

    public Leave getLeaveByLeaveId(String leaveId) {
        return leaveRepository.findByLeaveId(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave not found with ID: " + leaveId));
    }

    public List<Leave> getLeavesByStatus(String status) {
        if (!"Pending".equals(status) && !"Approved".equals(status) && !"Rejected".equals(status)) {
            throw new IllegalArgumentException("Status must be either 'Pending', 'Approved', or 'Rejected'");
        }
        return leaveRepository.findByStatus(status);
    }

    private String markPresentWithApprovedLeaveInAttendance(String employeeId, String leaveType, LocalDate leaveDate, LocalDate endDate, String reason) {
        try {
            String url = APPLY_LEAVE_URL + "/markPresentWithLeave"+"?employeeId=" + employeeId +
                    "&leaveType=" + leaveType +
                    "&startDate=" + leaveDate +
                    "&endDate=" + endDate +
                    "&reason=" + reason;

            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "Error while applying leave: " + e.getMessage();
        }
    }

    private String markApprovedLOPInAttendance(String employeeId, String leaveType, LocalDate leaveDate, LocalDate endDate, String reason) {
        try {
            String url = APPLY_LEAVE_URL +"/markApprovedLOP"+ "?employeeId=" + employeeId +
                    "&leaveType=" + leaveType +
                    "&startDate=" + leaveDate +
                    "&endDate=" + endDate +
                    "&reason=" + reason;

            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "Error while applying leave: " + e.getMessage();
        }
    }

    public List<Leave> getLeavesByEmployeeId(String employeeId) {
        List<Leave> leaves = leaveRepository.findByEmployeeId(employeeId);
        return leaves;
    }
} 