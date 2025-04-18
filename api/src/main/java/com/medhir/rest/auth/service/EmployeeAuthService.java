package com.medhir.rest.auth.service;

import com.medhir.rest.auth.dto.EmployeeAuthRequest;
import com.medhir.rest.auth.model.EmployeeAuth;
import com.medhir.rest.auth.repository.EmployeeAuthRepository;
import com.medhir.rest.config.JwtUtil;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeRepository;
import com.medhir.rest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeAuthService {
    private final EmployeeAuthRepository employeeAuthRepository;
    private final EmployeeRoleService employeeRoleService;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public EmployeeAuth registerEmployee(String employeeId, String email,String phone) {
        // Check if employee already registered
        if (employeeAuthRepository.findByEmployeeId(employeeId).isPresent() ||
            employeeAuthRepository.findByEmail(email).isPresent()) {
            throw new ResourceNotFoundException("Employee already registered");
        }

        EmployeeAuth employeeAuth = new EmployeeAuth();
        employeeAuth.setEmployeeId(employeeId);
        employeeAuth.setEmail(email);
        employeeAuth.setPassword(passwordEncoder.encode(phone)); // Use phone number as password

        return employeeAuthRepository.save(employeeAuth);
    }

    public Map<String, Object> login(EmployeeAuthRequest request) {
        // Find auth record by email
        EmployeeAuth employeeAuth = employeeAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid credentials"));

        // Get employee details to verify phone number
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeAuth.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Verify password (phone number)
        if (!passwordEncoder.matches(employee.getPhone(), employeeAuth.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        // Get current roles from employee record
        Set<String> roles = employeeRoleService.getEmployeeRoles(employeeAuth.getEmployeeId());

        Map<String, Object> response = new HashMap<>();
        response.put("employeeId", employeeAuth.getEmployeeId());
        response.put("roles", roles);
        response.put("token", generateToken(employeeAuth.getEmail(), roles));

        return response;
    }

    private String generateToken(String email, Set<String> roles) {
        return jwtUtil.generateTokenWithStringRoles(email, roles);
    }
} 