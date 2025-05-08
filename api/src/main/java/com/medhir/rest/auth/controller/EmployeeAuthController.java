package com.medhir.rest.auth.controller;

import com.medhir.rest.auth.dto.LoginRequest;
import com.medhir.rest.auth.dto.LoginResponse;
import com.medhir.rest.auth.model.EmployeeAuth;
import com.medhir.rest.auth.repository.EmployeeAuthRepository;
import com.medhir.rest.auth.service.EmployeeAuthService;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class EmployeeAuthController {

    private final EmployeeAuthService employeeAuthService;
    private final EmployeeRepository employeeRepository;
    private final EmployeeAuthRepository employeeAuthRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = employeeAuthService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{email}")
    public ResponseEntity<?> checkAuthRecord(@PathVariable String email) {
        try {
            EmployeeAuth auth = employeeAuthService.findByEmail(email);
            return ResponseEntity.ok(auth);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("No auth record found for email: " + email);
        }
    }

    @PostMapping("/create/{employeeId}")
    public ResponseEntity<?> createAuthRecord(@PathVariable String employeeId) {
        try {
            EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            employeeAuthService.createEmployeeAuth(employee);
            return ResponseEntity.ok("Auth record created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating auth record: " + e.getMessage());
        }
    }

    @PostMapping("/recreate/{employeeId}")
    public ResponseEntity<?> recreateAuthRecord(@PathVariable String employeeId) {
        try {
            EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            
            // Delete existing auth record if any
            employeeAuthRepository.findByEmployeeId(employeeId)
                    .ifPresent(auth -> employeeAuthRepository.delete(auth));
            
            // Create new auth record
            employeeAuthService.createEmployeeAuth(employee);
            
            return ResponseEntity.ok("Auth record recreated successfully. Use phone number: " + employee.getPhone());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error recreating auth record: " + e.getMessage());
        }
    }
} 