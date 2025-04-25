package com.medhir.rest.settings.department;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "departments")
public class DepartmentModel {

    @Id
    @JsonIgnore
    private String id;

    private String departmentId;

    @NotBlank(message = "Department name is required")
    private String name;

    @NotBlank(message = "Company ID is required")
    private String companyId;

    private String description;

    private String departmentHead;

    @NotBlank(message = "Leave policy name is required")
    private String leavePolicy;

    private String weeklyHolidays;

    private String createdAt;
    private String updatedAt;
}