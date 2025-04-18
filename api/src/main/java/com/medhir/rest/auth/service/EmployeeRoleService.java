package com.medhir.rest.auth.service;

import com.medhir.rest.auth.repository.EmployeeRoleRepository;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeRoleService {

    private final EmployeeRoleRepository employeeRoleRepository;

    public Set<String> getEmployeeRoles(String employeeId) {
        return employeeRoleRepository.findByEmployeeId(employeeId)
                .map(EmployeeModel::getRoles)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }
} 