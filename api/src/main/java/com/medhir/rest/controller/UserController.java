package com.medhir.rest.controller;

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

    // Get all users for dropdown
    @GetMapping
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Create a new user
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@Valid @RequestBody UserModel user) {
        UserModel savedUser = userService.createUser(user);
        return ResponseEntity.ok(Map.of(
                "message", "User created successfully!",
                "user", savedUser
        ));
    }
}
