package com.medhir.rest.exception;

import lombok.Data;

import java.util.List;

@Data
public class ValidationErrorResponse {
    private String message;
    private List<ValidationError> errors;

    @Data
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }

    public ValidationErrorResponse(String message, List<ValidationError> errors) {
        this.message = message;
        this.errors = errors;
    }
} 