package com.medhir.rest.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password; // This will be the phone number
} 