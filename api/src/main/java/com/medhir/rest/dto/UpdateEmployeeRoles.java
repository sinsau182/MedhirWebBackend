package com.medhir.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateEmployeeRoles {
    private List<String> roles;
    private String operation; // e.g., "ADD" or "REMOVE"
}
