package com.medhir.rest.auth.service;

import com.medhir.rest.auth.dto.EmployeeAuthRequest;
import com.medhir.rest.auth.dto.LoginRequest;
import com.medhir.rest.auth.dto.LoginResponse;
import com.medhir.rest.auth.model.EmployeeAuth;
import com.medhir.rest.auth.repository.EmployeeAuthRepository;
import com.medhir.rest.config.JwtUtil;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeRepository;
import com.medhir.rest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeAuthService implements UserDetailsService {
    private final EmployeeAuthRepository employeeAuthRepository;
    private final EmployeeRoleService employeeRoleService;
    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return employeeAuthRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public EmployeeAuth registerEmployee(String employeeId, String email, String phone) {
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

    public EmployeeAuth findByEmail(String email) {
        return employeeAuthRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No auth record found for email: " + email));
    }

    private String normalizePhoneNumber(String phone) {
        if (phone == null) {
            return "";
        }
        // Remove any non-digit characters and trim
        return phone.replaceAll("[^0-9]", "").trim();
    }

    public LoginResponse authenticate(LoginRequest request) {
        log.info("Attempting authentication for email: {}", request.getEmail());
        
        // Find auth record by email
        EmployeeAuth employeeAuth = employeeAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.error("No auth record found for email: {}", request.getEmail());
                    throw new RuntimeException("No auth record found for email: " + request.getEmail());
                });

        // Get employee details to show the actual phone number
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeAuth.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        // Debug logging for phone numbers
        log.info("Raw input phone: {}", request.getPassword());
        log.info("Raw stored phone: {}", employee.getPhone());
        
        // Normalize the input phone number
        String normalizedInputPhone = normalizePhoneNumber(request.getPassword());
        String normalizedStoredPhone = normalizePhoneNumber(employee.getPhone());
        
        log.info("Normalized input phone: {}", normalizedInputPhone);
        log.info("Normalized stored phone: {}", normalizedStoredPhone);
        
        // First verify if the phone numbers match exactly
        if (!normalizedInputPhone.equals(normalizedStoredPhone)) {
            log.error("Phone number mismatch. Input: {}, Expected: {}", normalizedInputPhone, normalizedStoredPhone);
            throw new RuntimeException("Invalid credentials. Expected phone number: " + normalizedStoredPhone);
        }

        // If phone numbers match, recreate the auth record to ensure correct encoding
        log.info("Phone numbers match, recreating auth record");
        employeeAuthRepository.delete(employeeAuth);
        createEmployeeAuth(employee);
        
        // Get the new auth record
        employeeAuth = employeeAuthRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Failed to create auth record"));
        
        log.info("New encoded password: {}", employeeAuth.getPassword());

        // Get roles
        Set<String> roles = employeeRoleService.getEmployeeRoles(employeeAuth.getEmployeeId());
        List<String> roleList = roles.stream()
                .map(role -> "ROLE_" + role)
                .collect(Collectors.toList());
        
        log.info("Roles found for employee {}: {}", employeeAuth.getEmployeeId(), roleList);

        // Generate JWT token
        String token = jwtService.generateToken(employeeAuth);
        log.info("JWT token generated successfully for employee: {}", employeeAuth.getEmployeeId());

        return LoginResponse.builder()
                .token(token)
                .roles(roleList)
                .employeeId(employeeAuth.getEmployeeId())
                .build();
    }

    public void createEmployeeAuth(EmployeeModel employee) {
        log.info("Creating auth record for employee: {}", employee.getEmployeeId());
        log.info("Employee phone number: {}", employee.getPhone());
        
        // Check if auth record already exists
        if (employeeAuthRepository.findByEmployeeId(employee.getEmployeeId()).isPresent()) {
            log.warn("Auth record already exists for employee: {}", employee.getEmployeeId());
            return;
        }

        // Normalize phone number before encoding
        String normalizedPhone = normalizePhoneNumber(employee.getPhone());
        log.info("Normalized phone number: {}", normalizedPhone);
        String encodedPassword = passwordEncoder.encode(normalizedPhone);
        log.info("Encoded phone number: {}", encodedPassword);

        EmployeeAuth employeeAuth = EmployeeAuth.builder()
                .employeeId(employee.getEmployeeId())
                .email(employee.getEmailPersonal())
                .password(encodedPassword)
                .build();
        
        employeeAuthRepository.save(employeeAuth);
        log.info("Auth record created successfully for employee: {}", employee.getEmployeeId());
    }

    private String generateToken(String email, Set<String> roles) {
        return jwtUtil.generateTokenWithStringRoles(email, roles);
    }

    public EmployeeAuth findByEmployeeId(String employeeId) {
        return employeeAuthRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new UsernameNotFoundException("No auth record found for employee: " + employeeId));
    }

    public void delete(EmployeeAuth auth) {
        employeeAuthRepository.delete(auth);
    }
} 