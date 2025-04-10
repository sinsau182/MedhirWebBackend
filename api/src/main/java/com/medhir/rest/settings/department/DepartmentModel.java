package com.medhir.rest.settings.department;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "departments")
public class DepartmentModel {

    @Id
    private String id;

    private String departmentId;

    @NotBlank(message = "Department name is required")
    private String name;

    private String description;

    private String departmentHead;

    @NotBlank(message = "Leave policy name is required")
    private String leavePolicy;

    private String weeklyHolidays;

    private String createdAt;
    private String updatedAt;
}