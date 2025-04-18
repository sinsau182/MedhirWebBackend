package com.medhir.rest.auth.controller;

import com.medhir.rest.auth.dto.EmployeeAuthRequest;
import com.medhir.rest.auth.service.EmployeeAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/employee-auth")
@RequiredArgsConstructor
public class EmployeeAuthController {

    private final EmployeeAuthService employeeAuthService;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody EmployeeAuthRequest request) {
        return  employeeAuthService.login(request);
    }
} 