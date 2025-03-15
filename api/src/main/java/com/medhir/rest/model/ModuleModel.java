package com.medhir.rest.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModuleModel {

    @Id
    private String id;

    @NotBlank(message = "Module name cannot be empty")
    private String moduleName;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    // This ensures userId is not stored in MongoDB
    @NotBlank(message = "User ID cannot be empty")
    private String userId;

    @DBRef
    private UserModel user; // Store a single user as admin
}
