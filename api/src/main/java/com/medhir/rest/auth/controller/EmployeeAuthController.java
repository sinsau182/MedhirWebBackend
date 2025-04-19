package com.medhir.rest.auth.controller;

import com.medhir.rest.auth.dto.EmployeeAuthRequest;
import com.medhir.rest.auth.dto.UpdatePasswordRequest;
import com.medhir.rest.auth.model.EmployeeAuth;
import com.medhir.rest.auth.service.EmployeeAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/employee-auth")
@RequiredArgsConstructor
public class EmployeeAuthController {

    private final EmployeeAuthService employeeAuthService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody EmployeeAuthRequest request) {
        return ResponseEntity.ok(employeeAuthService.login(request));
    }

    @PutMapping("/employees/{employeeId}/password")
    public ResponseEntity<EmployeeAuth> updatePassword(
            @PathVariable String employeeId,
            @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(employeeAuthService.updatePassword(
                employeeId,
                request.getOldPassword(),
                request.getNewPassword()
        ));
    }
} 