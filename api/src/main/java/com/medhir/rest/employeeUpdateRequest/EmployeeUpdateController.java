package com.medhir.rest.employeeUpdateRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/employee")
public class EmployeeUpdateController {

    @Autowired
    private EmployeeUpdateService employeeUpdateService;

    @Autowired
    private EmployeeRepository employeeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PutMapping("/update-request")
    public ResponseEntity<Map<String, Object>> requestUpdate(
            @RequestParam("updateRequest") String updateRequestJson,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestParam(value = "aadharImage", required = false) MultipartFile aadharImage,
            @RequestParam(value = "panImage", required = false) MultipartFile panImage,
            @RequestParam(value = "passportImage", required = false) MultipartFile passportImage,
            @RequestParam(value = "drivingLicenseImage", required = false) MultipartFile drivingLicenseImage,
            @RequestParam(value = "voterIdImage", required = false) MultipartFile voterIdImage,
            @RequestParam(value = "passbookImage", required = false) MultipartFile passbookImage) throws Exception {

        // Convert JSON string to EmployeeUpdateRequest object
        EmployeeUpdateRequest updateRequest = objectMapper.readValue(updateRequestJson, EmployeeUpdateRequest.class);

        // Manually validate the update request object
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<EmployeeUpdateRequest>> violations = validator.validate(updateRequest);

        if (!violations.isEmpty()) {
            Map<String, String> errors = new HashMap<>();
            for (ConstraintViolation<EmployeeUpdateRequest> violation : violations) {
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Check if there's already a pending request
        Optional<EmployeeModel> existingEmployee = employeeRepository.findByEmployeeId(updateRequest.getEmployeeId());
        if (existingEmployee.isPresent()) {
            EmployeeModel employee = existingEmployee.get();
            if ("Pending".equals(employee.getUpdateStatus())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "You already have a pending update request. Please wait for HR to process your previous request."
                ));
            }
        }

        // Pass the deserialized object and images to the service layer
        EmployeeModel savedRequest = employeeUpdateService.createUpdateRequest(
                updateRequest, profileImage, aadharImage, panImage, passportImage, drivingLicenseImage, voterIdImage, passbookImage);

        return ResponseEntity.ok(Map.of(
                "message", "Update request submitted for approval"
        ));
    }
}
