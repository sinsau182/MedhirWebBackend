package com.medhir.rest.settings.leaveSettings.leaveType;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leave-types")
@CrossOrigin(origins = "*")
public class LeaveTypeController {

    @Autowired
    private LeaveTypeService leaveTypeService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createLeaveType(@Valid @RequestBody LeaveTypeModel leaveType) {
        leaveType.setLeaveTypeId(null); // Clear any existing leaveTypeId
        LeaveTypeModel createdLeaveType = leaveTypeService.createLeaveType(leaveType);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leave type created successfully");
        response.put("leaveTypeId", createdLeaveType.getLeaveTypeId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LeaveTypeModel>> getAllLeaveTypes() {
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypes());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<LeaveTypeModel>> getLeaveTypesByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(leaveTypeService.getLeaveTypesByCompanyId(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeaveTypeModel> getLeaveTypeById(@PathVariable String id) {
        return ResponseEntity.ok(leaveTypeService.getLeaveTypeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateLeaveType(
            @PathVariable String id,
            @Valid @RequestBody LeaveTypeModel leaveType) {
        leaveType.setLeaveTypeId(null); // Clear any existing leaveTypeId
        leaveTypeService.updateLeaveType(id, leaveType);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leave type updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteLeaveType(@PathVariable String id) {
        leaveTypeService.deleteLeaveType(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Leave type deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/{leaveTypeName}")
    public ResponseEntity<Boolean> checkLeaveTypeExists(@PathVariable String leaveTypeName) {
        boolean exists = leaveTypeService.existsByLeaveTypeName(leaveTypeName);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}