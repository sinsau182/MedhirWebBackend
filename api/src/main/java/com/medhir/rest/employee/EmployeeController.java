package com.medhir.rest.employee;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.medhir.rest.dto.CompanyEmployeeDTO;
import com.medhir.rest.dto.ManagerEmployeeDTO;
import com.medhir.rest.dto.UpdateEmployeeRoles;
import com.medhir.rest.dto.UserCompanyDTO;
import com.medhir.rest.employee.dto.EmployeeAttendanceDetailsDTO;
import com.medhir.rest.employee.dto.EmployeeWithLeaveDetailsDTO;
import com.medhir.rest.service.UserService;
import com.medhir.rest.utils.GeneratedId;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.HashMap;

@RestController
@RequestMapping("/")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserService userService;

    @Autowired
    private GeneratedId generatedId;

    // Generate Employee ID
    @GetMapping("/hradmin/generate-employee-id/{companyId}")
    public ResponseEntity<String> generateEmployeeId(@PathVariable String companyId) {
        String generatedId = employeeService.generateEmployeeId(companyId);
        return ResponseEntity.ok(generatedId);
    }

    // Get all companies associated with admins
    @GetMapping("/hradmin/companies/{employeeId}")
    public ResponseEntity<List<UserCompanyDTO>> getUserCompanies(@PathVariable String employeeId) {
        List<UserCompanyDTO> companies = employeeService.getEmployeeCompanies(employeeId);
        return ResponseEntity.ok(companies);
    }

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
        EmployeeWithLeaveDetailsDTO createdEmployee = employeeService.createEmployee(
                employee, profileImage, aadharImage, panImage, passportImage, drivingLicenseImage, voterIdImage, passbookImage);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee created successfully");
        response.put("employee", createdEmployee);
        return ResponseEntity.ok(response);
    }

    // Get All Employees
    @GetMapping("/employee")
    public ResponseEntity<List<EmployeeWithLeaveDetailsDTO>> getAllEmployees(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Get All Employees (HR Admin endpoint)
    @GetMapping("/hradmin/employees")
    public ResponseEntity<List<EmployeeWithLeaveDetailsDTO>> getAllEmployeesHRAdmin(){
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    // Get All Employees with minimal fields (name and employeeId)
    @GetMapping("/employees/minimal")
    public ResponseEntity<List<Map<String, String>>> getAllEmployeesMinimal() {
        return ResponseEntity.ok(employeeService.getAllEmployeesMinimal());
    }

    // Get All Employees by Company ID
    // @GetMapping("/hradmin/companies/{companyId}/employees")
    // public ResponseEntity<List<EmployeeWithLeaveDetailsDTO>> getEmployeesByCompanyId(@PathVariable String companyId){
    //     return ResponseEntity.ok(employeeService.getEmployeesByCompanyId(companyId));
    // }

    // Get All Employees by Company ID with additional details
    @GetMapping("/hradmin/companies/{companyId}/employees")
    public ResponseEntity<List<CompanyEmployeeDTO>> getAllEmployeesByCompanyIdWithDetails(@PathVariable String companyId) {
        return ResponseEntity.ok(employeeService.getAllEmployeesByCompanyIdWithDetails(companyId));
    }

    // Get Employee by Employee ID
    @GetMapping("/employee/id/{employeeId}")
    public ResponseEntity<Optional<EmployeeWithLeaveDetailsDTO>> getEmployeeById(@PathVariable String employeeId){
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
        EmployeeWithLeaveDetailsDTO updatedEmployee = employeeService.updateEmployee(
                employeeId, employee, profileImage, aadharImage, panImage, passportImage, drivingLicenseImage, voterIdImage, passbookImage);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Employee updated successfully");
        response.put("employee", updatedEmployee);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employees/manager/{managerId}")
    public ResponseEntity<List<ManagerEmployeeDTO>> getEmployeesByManagerId(@PathVariable String managerId){
        List<ManagerEmployeeDTO> employees = employeeService.getEmployeesByManager(managerId);
        return ResponseEntity.ok(employees);
    }

    // Delete Employee by Employee ID
    @DeleteMapping("/hradmin/employees/{employeeId}")
    public ResponseEntity<Map<String, String>> deleteEmployee(
            @PathVariable String employeeId) {
        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully"));
    }

    @GetMapping("/departments/{departmentId}/managers")
    public ResponseEntity<List<Map<String, String>>> getManagersByDepartment(
            @PathVariable String departmentId) {
        return ResponseEntity.ok(employeeService.getManagersByDepartment(departmentId));
    }

    @PutMapping("/hradmin/employees/{employeeId}/roles")
    public ResponseEntity<Map<String, Object>> updateEmployeeRole(
            @PathVariable String employeeId,
            @RequestBody UpdateEmployeeRoles request) {

        EmployeeModel updatedEmployee = employeeService.updateEmployeeRole(
                employeeId,
                request.getRoles(),
                request.getOperation()
        );

        return ResponseEntity.ok(Map.of(
                "message", "Employee roles updated successfully"
        ));
    }

    @GetMapping("/employee/{employeeId}/attendance-details")
    public ResponseEntity<EmployeeAttendanceDetailsDTO> getEmployeeAttendanceDetails(@PathVariable String employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeAttendanceDetails(employeeId));
    }

}
