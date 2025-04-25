package com.medhir.rest.updates;

import lombok.Data;

@Data
public class UpdateRequest {
    private String employeeId;
    private String message;
    private String flag;
}
