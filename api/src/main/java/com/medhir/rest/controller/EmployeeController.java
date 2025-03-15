package com.medhir.rest.controller;

import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hradmin/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //Create Employee
    @PostMapping
    public ResponseEntity<Map<String,Object>> createEmployee(@Valid @RequestBody EmployeeModel employee) {
        EmployeeModel savedEmployee = employeeService.createEmployee(employee);
        return ResponseEntity.ok(Map.of(
                "message","Created Employee Successfully",
                "Employee",savedEmployee
        ));
    }

    // Get All Employees
    @GetMapping
    public ResponseEntity<List<EmployeeModel>> getAllEmployees() {
        List<EmployeeModel> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateEmployee(@PathVariable String id, @Valid @RequestBody EmployeeModel employee) {
        EmployeeModel updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(Map.of(
                "message", "Updated Employee Successfully",
                "Employee", updatedEmployee
        ));
    }

}
