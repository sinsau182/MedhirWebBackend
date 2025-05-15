package com.medhir.rest.leave.controller;

import com.medhir.rest.leave.dto.LeaveWithEmployeeDetails;
import com.medhir.rest.leave.service.LeaveApplicationService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/manager/leave")
public class ManagerLeaveController {

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    @GetMapping("/status/{status}/{managerId}")
    public ResponseEntity<?> getTeamMembersLeavesByStatus(
            @PathVariable @NotBlank(message = "Status is required") String status,
            @PathVariable @NotBlank(message = "Manager ID is required") String managerId) {
        try {
            List<LeaveWithEmployeeDetails> leaves = leaveApplicationService.getLeavesByManagerIdAndStatus(managerId, status);
            return ResponseEntity.ok(Map.of(
                    "count", leaves.size(),
                    "leaves", leaves));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 