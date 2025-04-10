package com.medhir.rest.settings.department;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<Map<String, String>> createDepartment(@Valid @RequestBody DepartmentModel department) {
        department.setDepartmentId(null); // Clear any existing departmentId
        DepartmentModel createdDepartment = departmentService.createDepartment(department);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department created successfully");
        response.put("departmentId", createdDepartment.getDepartmentId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DepartmentModel>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentModel> getDepartmentById(@PathVariable String id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateDepartment(
            @PathVariable String id,
            @Valid @RequestBody DepartmentModel department) {
        department.setDepartmentId(null); // Clear any existing departmentId
        departmentService.updateDepartment(id, department);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDepartment(@PathVariable String id) {
        departmentService.deleteDepartment(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Department deleted successfully");
        return ResponseEntity.ok(response);
    }
}
