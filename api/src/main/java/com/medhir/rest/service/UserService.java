package com.medhir.rest.service;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.UserModel;
import com.medhir.rest.repository.UserRepository;
import com.medhir.rest.utils.GeneratedId;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private GeneratedId generatedId;

    @Autowired
    private CompanyService companyService;

    // Get all users
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    // Create a user
    public UserModel createUser(UserModel user) {
        if(userRepository.findByemail(user.getEmail()).isPresent()){
            throw new DuplicateResourceException("User With Email : " + user.getEmail() + " already exits");
        }
        if(userRepository.findByPhone(user.getPhone()).isPresent()){
            throw new DuplicateResourceException("User With Phone : " + user.getPhone() + " already exits");
        }
        user.setUserId(generatedId.generateId("UID", UserModel.class, "userId"));

        // create username and password and set roles



        return userRepository.save(user);
    }

    // Update a user
    public void updateUser(String userId, UserModel updatedUser) {
        UserModel existingUser = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));

        // Check if email is being changed and if it already exists
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.findByemail(updatedUser.getEmail()).isPresent()) {
                throw new DuplicateResourceException("User with email " + updatedUser.getEmail() + " already exists");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Check if phone is being changed and if it already exists
        if (updatedUser.getPhone() != null && !updatedUser.getPhone().equals(existingUser.getPhone())) {
            if (userRepository.findByPhone(updatedUser.getPhone()).isPresent()) {
                throw new DuplicateResourceException("User with phone " + updatedUser.getPhone() + " already exists");
            }
            existingUser.setPhone(updatedUser.getPhone());
        }

        // Update other fields if provided
        if (updatedUser.getName() != null) {
            existingUser.setName(updatedUser.getName());
        }
        if (updatedUser.getModuleIds() != null) {
            existingUser.setModuleIds(updatedUser.getModuleIds());
        }

        userRepository.save(existingUser);
    }

    // Delete a user
    public void deleteUser(String userId) {
        UserModel user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
        userRepository.delete(user);
    }

//    public List<UserCompanyDTO> getUserCompanies(String userId) {
//        UserModel user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " not found"));
//
//        if (user.getModuleIds() == null || user.getModuleIds().isEmpty()) {
//            return List.of();
//        }
//
//        // Get all modules for the user
//        List<ModuleModel> modules = user.getModuleIds().stream()
//                .map(moduleId -> moduleRepository.findByModuleId(moduleId))
//                .filter(Optional::isPresent)
//                .map(Optional::get)
//                .toList();
//
//        // Get unique company IDs from modules
//        Set<String> companyIds = modules.stream()
//                .filter(module -> module.getCompanyId() != null)
//                .map(ModuleModel::getCompanyId)
//                .collect(Collectors.toSet());
//
//        // Get company details for each company ID
//        return companyIds.stream()
//                .map(companyId -> {
//                    try {
//                        CompanyModel company = companyService.getCompanyById(companyId).orElseThrow();
//                        return new UserCompanyDTO(company.getCompanyId(), company.getName(),company.getColorCode());
//                    } catch (ResourceNotFoundException e) {
//                        return new UserCompanyDTO(companyId, "Unknown Company", "Unknown Color");
//                    }
//                })
//                .collect(Collectors.toList());
//    }
}