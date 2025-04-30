package com.medhir.rest.leave.controller;

import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.leave.dto.LeaveWithEmployeeDetails;
import com.medhir.rest.leave.dto.UpdateLeaveStatusRequest;
import com.medhir.rest.leave.model.LeaveModel;
import com.medhir.rest.leave.service.LeaveApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave")
public class LeaveApplicationController {

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(@Valid @RequestBody LeaveModel request) {
        try {
            // Validate leave type for regular leave
            // if ("Leave".equals(request.getLeaveName()) && (request.getLeaveType() == null
            // || request.getLeaveType().isEmpty())) {
            // return ResponseEntity.badRequest().body(Map.of("error", "Leave type is
            // required for regular leave"));
            // }
            request.setLeaveType("Annual Leave");

            LeaveModel leave = leaveApplicationService.applyLeave(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Leave application submitted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update-status")
    public ResponseEntity<?> updateLeaveStatus(@RequestBody UpdateLeaveStatusRequest request) {
        try {
            LeaveModel leave = leaveApplicationService.updateLeaveStatus(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Leave status updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<?> getLeaveById(@PathVariable String leaveId) {
        try {
            LeaveModel leave = leaveApplicationService.getLeaveByLeaveId(leaveId);
            return ResponseEntity.ok(leave);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{companyId}/{status}")
    public ResponseEntity<?> getLeavesByStatus(@PathVariable String companyId, @PathVariable String status) {
        try {
            List<LeaveWithEmployeeDetails> leaves = leaveApplicationService.getLeavesByStatus(companyId, status);
            return ResponseEntity.ok(Map.of(
                    "count", leaves.size(),
                    "leaves", leaves));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getLeavesByEmployeeId(@PathVariable String employeeId) {
        try {
            List<LeaveModel> leaves = leaveApplicationService.getLeavesByEmployeeId(employeeId);
            return ResponseEntity.ok(leaves);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}