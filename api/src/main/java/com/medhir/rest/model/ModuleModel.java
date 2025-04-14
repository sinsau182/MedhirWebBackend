package com.medhir.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "modules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModuleModel {

    @Id
    @JsonIgnore
    private String id;

    private String moduleId;

    @NotBlank(message = "Module name cannot be empty")
    private String moduleName;

    @NotBlank(message = "Description cannot be empty")
    private String description;

    private List<String> employeeIds;

    private String companyId;
}