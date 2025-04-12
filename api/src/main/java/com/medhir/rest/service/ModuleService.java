package com.medhir.rest.service;

import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.model.UserModel;
import com.medhir.rest.repository.ModuleRepository;
import com.medhir.rest.repository.UserRepository;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<ModuleModel> getAllModules() {
        return moduleRepository.findAll();
    }
}