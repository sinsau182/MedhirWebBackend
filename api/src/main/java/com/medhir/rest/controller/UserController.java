package com.medhir.rest.controller;

import com.medhir.rest.dto.RegisterAdminRequest;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeService;
import com.medhir.rest.model.UserModel;
import com.medhir.rest.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/superadmin/modules/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmployeeService employeeService;

    // Get all users for dropdown
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody RegisterAdminRequest user) {
        EmployeeModel savedUser = employeeService.registerAdminAsEmployee(user);
        return ResponseEntity.ok(Map.of(
                "message", "User created successfully!",
                "user", savedUser
        ));
    }

    // Update an existing user
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, String>> updateUser(
            @PathVariable String userId,
            @Valid @RequestBody UserModel updatedUser) {
        userService.updateUser(userId, updatedUser);
        return ResponseEntity.ok(Map.of(
                "message", "User updated successfully!"
        ));
    }

    // Delete a user
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of(
                "message", "User deleted successfully!"
        ));
    }


}
