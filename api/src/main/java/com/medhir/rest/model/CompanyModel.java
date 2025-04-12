package com.medhir.rest.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "companies") // Define MongoDB collection
public class CompanyModel {

    @Id
    private String id; //  MongoDB uses String IDs by default

    private String companyId;


    @NotBlank(message = "Company name cannot be empty")
    @Size(min = 2, message = "Company name must have at least 2 characters")
    private String name;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Indexed(unique = true) // Ensure unique email in MongoDB
    private String email;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Indexed(unique = true) //  Ensure unique phone in MongoDB
    private String phone;

    @NotBlank(message = "GST number is required")
    @Pattern(
            regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[0-9A-Z]{1}[Z]{1}[0-9A-Z]{1}$",
            message = "Invalid GST format. Must be a 15-character alphanumeric GSTIN.")
    private String gst;

    @NotBlank(message = "Registration address cannot be empty")
    private String regAdd;
}