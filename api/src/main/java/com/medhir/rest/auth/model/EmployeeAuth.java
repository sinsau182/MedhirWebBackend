package com.medhir.rest.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "employee_auth")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAuth {
    @Id
    private String id;

    @Indexed(unique = true)
    private String employeeId;

    @Indexed(unique = true)
    private String email;

    private String password;
} 