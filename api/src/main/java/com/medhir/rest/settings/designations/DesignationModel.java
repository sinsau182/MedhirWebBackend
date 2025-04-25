package com.medhir.rest.settings.designations;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "designations")
public class DesignationModel {

    @Id
    @JsonIgnore
    private String id;

    private String designationId;

    @NotBlank(message = "Designation name is required")
    private String name;

    private String description;

    @NotBlank(message = "Department is required")
    private String department;

    private boolean isManager;
    private boolean overtimeEligible;

    private String createdAt;
    private String updatedAt;
}