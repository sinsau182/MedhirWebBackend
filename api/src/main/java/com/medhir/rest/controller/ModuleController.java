package com.medhir.rest.controller;

import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.service.ModuleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<ModuleModel> getAllModules() {
        return moduleService.getAllModules();
    }
}
