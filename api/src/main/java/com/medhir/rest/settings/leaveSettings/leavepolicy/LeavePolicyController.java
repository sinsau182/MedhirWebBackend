package com.medhir.rest.settings.leaveSettings.leavepolicy;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave-policies")
public class LeavePolicyController {

    @Autowired
    private LeavePolicyService leavePolicyService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createLeavePolicy(@Valid @RequestBody LeavePolicyModel leavePolicy) {
        leavePolicy.setLeavePolicyId(null); // Clear any existing leavePolicyId
        LeavePolicyModel createdLeavePolicy = leavePolicyService.createLeavePolicy(leavePolicy);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leave policy created successfully");
        response.put("leavePolicyId", createdLeavePolicy.getLeavePolicyId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LeavePolicyModel>> getAllLeavePolicies() {
        return ResponseEntity.ok(leavePolicyService.getAllLeavePolicies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeavePolicyModel> getLeavePolicyById(@PathVariable String id) {
        return ResponseEntity.ok(leavePolicyService.getLeavePolicyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateLeavePolicy(
            @PathVariable String id,
            @Valid @RequestBody LeavePolicyModel leavePolicy) {
        leavePolicy.setLeavePolicyId(null); // Clear any existing leavePolicyId
        leavePolicyService.updateLeavePolicy(id, leavePolicy);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leave policy updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLeavePolicy(@PathVariable String id) {
        leavePolicyService.deleteLeavePolicy(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leave policy deleted successfully");
        return ResponseEntity.ok(response);
    }
} 