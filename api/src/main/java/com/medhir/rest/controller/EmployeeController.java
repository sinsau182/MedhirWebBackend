package com.medhir.rest.controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @PostMapping("/hradmin/employees")
    public ResponseEntity<Map<String, Object>> createEmployee(
            @RequestParam("employee") String employeeJson, // Receive employee as JSON string
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "aadharImage", required = false) MultipartFile aadharImage,
            @RequestParam(value = "panImage", required = false) MultipartFile panImage,
            @RequestParam(value = "passportImage", required = false) MultipartFile passportImage,
            @RequestParam(value = "drivingLicenseImage", required = false) MultipartFile drivingLicenseImage,
            @RequestParam(value = "voterIdImage", required = false) MultipartFile voterIdImage,
            @RequestParam(value = "passbookImage", required = false) MultipartFile passbookImage) throws Exception {

        // Convert JSON string to EmployeeModel object
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Required for LocalDate
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        EmployeeModel employee = objectMapper.readValue(employeeJson, EmployeeModel.class);

        // Manually validate the employee object
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<EmployeeModel>> violations = validator.validate(employee);

        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<EmployeeModel> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Pass the deserialized object to the service layer
        EmployeeModel savedEmployee = employeeService.createEmployee(
                employee, profileImage, aadharImage, panImage, passportImage, drivingLicenseImage, voterIdImage, passbookImage);

        return ResponseEntity.ok(Map.of(
                "message", "Created Employee Successfully"
        ));
    }


    // Get All Employees
    @GetMapping("/hradmin/employees")
    public ResponseEntity<List<EmployeeModel>> getAllEmployees() {
        List<EmployeeModel> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employee/id/{employeeId}")
    public ResponseEntity<Optional<EmployeeModel>> getEmployeeById(@PathVariable String employeeId){
        return ResponseEntity.ok(employeeService.getEmployeeById(employeeId));
    }

    @PutMapping("/hradmin/employees/{employeeId}")
    public ResponseEntity<Map<String, Object>> updateEmployee(
            @PathVariable String employeeId,
            @RequestParam("employee") String employeeJson, // Receive employee as JSON string
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "aadharImage", required = false) MultipartFile aadharImage,
            @RequestParam(value = "panImage", required = false) MultipartFile panImage,
            @RequestParam(value = "passportImage", required = false) MultipartFile passportImage,
            @RequestParam(value = "drivingLicenseImage", required = false) MultipartFile drivingLicenseImage,
            @RequestParam(value = "voterIdImage", required = false) MultipartFile voterIdImage,
            @RequestParam(value = "passbookImage", required = false) MultipartFile passbookImage) throws Exception {

        // Convert JSON string to EmployeeModel object
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Required for LocalDate
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        EmployeeModel employee = objectMapper.readValue(employeeJson, EmployeeModel.class);

        // Manually validate the employee object
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<EmployeeModel>> violations = validator.validate(employee);

        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<EmployeeModel> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Pass the deserialized object and images to the service layer
        EmployeeModel updatedEmployee = employeeService.updateEmployee(
                employeeId, employee, profileImage, aadharImage, panImage, passportImage, drivingLicenseImage, voterIdImage, passbookImage);

        return ResponseEntity.ok(Map.of(
                "message", "Updated Employee Successfully"
        ));
    }

    @GetMapping("/employees/managerId/{managerId}")
    public ResponseEntity<List<EmployeeModel>> getEmployeesByManagerId(@PathVariable String managerId){
        List<EmployeeModel> employees = employeeService.getEmployeesByReportingManager(managerId);
        return ResponseEntity.ok(employees);
    }


}
