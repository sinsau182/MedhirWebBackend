package com.medhir.rest.service;

import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.model.UserModel;
import com.medhir.rest.repository.ModuleRepository;
import com.medhir.rest.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private GeneratedId generatedId;

    public ModuleModel createModule(ModuleModel moduleModel) {
        // Validate company exists
        if (moduleModel.getCompanyId() != null) {
            companyService.getCompanyById(moduleModel.getCompanyId());
        }

        // Validate all users exist
        List<UserModel> users = new ArrayList<>();
        for (String userId : moduleModel.getUserIds()) {
            UserModel user = userRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
            users.add(user);
        }

        // Generate module ID
        moduleModel.setModuleId(generatedId.generateId("MID", ModuleModel.class, "moduleId"));

        // Save the module
        ModuleModel savedModule = moduleRepository.save(moduleModel);

        // Update user details with module ID
        for (UserModel user : users) {
            if (user.getModuleIds() == null) {
                user.setModuleIds(new ArrayList<>());
            }
            user.getModuleIds().add(savedModule.getModuleId());
            userRepository.save(user);
        }

        return savedModule;
    }

    public List<ModuleResponseDTO> getAllModules() {
        List<ModuleModel> modules = moduleRepository.findAll();
        return modules.stream().map(module -> {
            List<String> userNames = new ArrayList<>();
            if (module.getUserIds() != null) {
                userNames = module.getUserIds().stream()
                        .map(userId -> userRepository.findByUserId(userId)
                                .map(UserModel::getName)
                                .orElse("Unknown User"))
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
                    userNames,
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

        // Handle user updates
        if (updatedModule.getUserIds() != null) {
            // Remove module ID from old users who are no longer in the list
            if (existingModule.getUserIds() != null) {
                for (String oldUserId : existingModule.getUserIds()) {
                    if (!updatedModule.getUserIds().contains(oldUserId)) {
                        userRepository.findByUserId(oldUserId).ifPresent(user -> {
                            if (user.getModuleIds() != null) {
                                user.getModuleIds().remove(moduleId);
                                userRepository.save(user);
                            }
                        });
                    }
                }
            }

            // Validate all new users exist and update their module associations
            List<UserModel> users = new ArrayList<>();
            for (String userId : updatedModule.getUserIds()) {
                UserModel user = userRepository.findByUserId(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
                users.add(user);
                
                // Add module ID to user if not already present
                if (user.getModuleIds() == null) {
                    user.setModuleIds(new ArrayList<>());
                }
                if (!user.getModuleIds().contains(moduleId)) {
                    user.getModuleIds().add(moduleId);
                    userRepository.save(user);
                }
            }
            existingModule.setUserIds(updatedModule.getUserIds());
        }

        moduleRepository.save(existingModule);
    }

    public void deleteModule(String moduleId) {
        ModuleModel module = moduleRepository.findByModuleId(moduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Module with ID " + moduleId + " not found"));

        // Remove module ID from all associated users
        if (module.getUserIds() != null) {
            for (String userId : module.getUserIds()) {
                userRepository.findByUserId(userId).ifPresent(user -> {
                    if (user.getModuleIds() != null) {
                        user.getModuleIds().remove(moduleId);
                        userRepository.save(user);
                    }
                });
            }
        }

        moduleRepository.delete(module);
    }
}