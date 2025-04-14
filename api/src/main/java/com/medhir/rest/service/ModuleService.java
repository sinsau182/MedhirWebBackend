package com.medhir.rest.service;

import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeRepository;
import com.medhir.rest.repository.ModuleRepository;
import com.medhir.rest.utils.GeneratedId;
import com.medhir.rest.dto.ModuleResponseDTO;
import com.medhir.rest.model.CompanyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private GeneratedId generatedId;

    public ModuleModel createModule(ModuleModel moduleModel) {
        // Validate company exists
        if (moduleModel.getCompanyId() != null) {
            companyService.getCompanyById(moduleModel.getCompanyId());
        }

        // Validate all employees exist
        List<EmployeeModel> employees = new ArrayList<>();
        for (String employeeId : moduleModel.getEmployeeIds()) {
            EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));
            employees.add(employee);
        }

        // Generate module ID
        moduleModel.setModuleId(generatedId.generateId("MID", ModuleModel.class, "moduleId"));

        // Save the module
        ModuleModel savedModule = moduleRepository.save(moduleModel);

        // Update employee details with module ID
        for (EmployeeModel employee : employees) {
            if (employee.getModuleIds() == null) {
                employee.setModuleIds(new ArrayList<>());
            }
            employee.getModuleIds().add(savedModule.getModuleId());
            employeeRepository.save(employee);
        }

        return savedModule;
    }

    public List<ModuleResponseDTO> getAllModules() {
        List<ModuleModel> modules = moduleRepository.findAll();
        return modules.stream().map(module -> {
            List<String> employeeNames = new ArrayList<>();
            if (module.getEmployeeIds() != null) {
                employeeNames = module.getEmployeeIds().stream()
                        .map(employeeId -> employeeRepository.findByEmployeeId(employeeId)
                                .map(EmployeeModel::getName)
                                .orElse("Unknown Employee"))
                        .collect(Collectors.toList());
            }

            String companyName = null;
            if (module.getCompanyId() != null) {
                try {
                    Optional<CompanyModel> company = companyService.getCompanyById(module.getCompanyId());
                    companyName = company.get().getName();
                } catch (ResourceNotFoundException e) {
                    companyName = "Unknown Company";
                }
            }

            return new ModuleResponseDTO(
                    module.getModuleId(),
                    module.getModuleName(),
                    module.getDescription(),
                    employeeNames,
                    companyName
            );
        }).collect(Collectors.toList());
    }

    public void updateModule(String moduleId, ModuleModel updatedModule) {
        ModuleModel existingModule = moduleRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module with ID " + moduleId + " not found"));

        // Update only the allowed fields
        if (updatedModule.getModuleName() != null) {
            existingModule.setModuleName(updatedModule.getModuleName());
        }
        if (updatedModule.getDescription() != null) {
            existingModule.setDescription(updatedModule.getDescription());
        }
        if (updatedModule.getCompanyId() != null) {
            // Validate company exists
            companyService.getCompanyById(updatedModule.getCompanyId());
            existingModule.setCompanyId(updatedModule.getCompanyId());
        }

        // Handle employee updates
        if (updatedModule.getEmployeeIds() != null) {
            // Remove module ID from old employees who are no longer in the list
            if (existingModule.getEmployeeIds() != null) {
                for (String oldEmployeeId : existingModule.getEmployeeIds()) {
                    if (!updatedModule.getEmployeeIds().contains(oldEmployeeId)) {
                        employeeRepository.findByEmployeeId(oldEmployeeId).ifPresent(employee -> {
                            if (employee.getModuleIds() != null) {
                                employee.getModuleIds().remove(moduleId);
                                employeeRepository.save(employee);
                            }
                        });
                    }
                }
            }

            // Validate all new employees exist and update their module associations
            List<EmployeeModel> employees = new ArrayList<>();
            for (String employeeId : updatedModule.getEmployeeIds()) {
                EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));
                employees.add(employee);
                
                // Add module ID to employee if not already present
                if (employee.getModuleIds() == null) {
                    employee.setModuleIds(new ArrayList<>());
                }
                if (!employee.getModuleIds().contains(moduleId)) {
                    employee.getModuleIds().add(moduleId);
                    employeeRepository.save(employee);
                }
            }
            existingModule.setEmployeeIds(updatedModule.getEmployeeIds());
        }

        moduleRepository.save(existingModule);
    }

    public void deleteModule(String moduleId) {
        ModuleModel module = moduleRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module with ID " + moduleId + " not found"));

        // Remove module ID from all associated employees
        if (module.getEmployeeIds() != null) {
            for (String employeeId : module.getEmployeeIds()) {
                employeeRepository.findByEmployeeId(employeeId).ifPresent(employee -> {
                    if (employee.getModuleIds() != null) {
                        employee.getModuleIds().remove(moduleId);
                        employeeRepository.save(employee);
                    }
                });
            }
        }

        moduleRepository.delete(module);
    }
}