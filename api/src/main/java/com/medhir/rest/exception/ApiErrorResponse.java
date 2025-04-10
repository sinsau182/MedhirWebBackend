package com.medhir.rest.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiErrorResponse {
    private String timestamp;
    private int status;
    private String error;
    private String message;
    private Map<String, String> validationErrors;
} 