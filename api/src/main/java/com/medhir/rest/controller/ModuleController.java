package com.medhir.rest.controller;

import com.medhir.rest.dto.ModuleResponseDTO;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/superadmin/modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @PostMapping
    public Map<String, Object> createModule(@Valid @RequestBody ModuleModel moduleModel) {
        ModuleModel savedModule = moduleService.createModule(moduleModel);
        return Map.of(
                "message", "Module created successfully!",
                "module", savedModule
        );
    }

    @GetMapping
    public List<ModuleResponseDTO> getAllModules() {
        return moduleService.getAllModules();
    }

    @PutMapping("/{moduleId}")
    public ResponseEntity<Map<String, String>> updateModule(
            @PathVariable String moduleId,
            @Valid @RequestBody ModuleModel updatedModule) {
        moduleService.updateModule(moduleId, updatedModule);
        return ResponseEntity.ok(Map.of(
                "message", "Module updated successfully!"
        ));
    }

    @DeleteMapping("/{moduleId}")
    public ResponseEntity<Map<String, String>> deleteModule(@PathVariable String moduleId) {
        moduleService.deleteModule(moduleId);
        return ResponseEntity.ok(Map.of(
                "message", "Module deleted successfully!"
        ));
    }
//
//    @GetMapping("/employee/{employeeId}/companies")
//    public ResponseEntity<List<UserCompanyDTO>> getEmployeeCompanies(@PathVariable String employeeId) {
//        List<UserCompanyDTO> companies = moduleService.getEmployeeCompanies(employeeId);
//        return ResponseEntity.ok(companies);
//    }
}
