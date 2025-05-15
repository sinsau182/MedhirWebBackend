package com.medhir.rest.auth.controller;

import com.medhir.rest.auth.dto.PasswordChangeRequest;
import com.medhir.rest.auth.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth/password")
@RequiredArgsConstructor
public class PasswordController {
    private final PasswordService passwordService;

    @PostMapping("/change")
    public ResponseEntity<Map<String, String>> changePassword(
            Authentication authentication,
            @Valid @RequestBody PasswordChangeRequest request) {
        String email = authentication.getName();
        passwordService.changePassword(email, request);
        return ResponseEntity.ok(Map.of("message", "Password has been changed successfully"));
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> getPasswordStatus(Authentication authentication) {
        String email = authentication.getName();
        boolean isChanged = passwordService.isPasswordChanged(email);
        return ResponseEntity.ok(isChanged);
    }
} 