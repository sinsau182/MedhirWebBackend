package com.medhir.rest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
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
@Document(collection = "employees")
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON response
public class EmployeeModel {

    @Id
    private String id;

    @NotBlank(message = "Employee name cannot be empty")
    private String name;

    private String title; // Job Title

    @Email(message = "Invalid email format!")
    @Indexed(unique = true)
    private String email;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Indexed(unique = true)
    @NotBlank(message = "Phone number cannot be empty")
    private String phone;

    private String department;
    private String gender;
    private String reportingManager;
    private String permanentAddress;
    private String currentAddress;

    // ID Proofs Section
    @Valid
    private IdProofs idProofs;

    // Bank Details Section
    @Valid
    private BankDetails bankDetails;

    // Salary Details Section
    @Valid
    private SalaryDetails salaryDetails;

    // Nested class for ID Proofs
    @Getter
    @Setter
    public static class IdProofs {
        @Pattern(regexp = "\\d{12}", message = "Aadhar number must be exactly 12 digits")
        @Size(min = 0) // Allows empty values
        private String aadharNo = "";

        @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN number format")
        @Size(min = 0) // Allows empty values
        private String panNo = "";

        @Pattern(regexp = "^[A-Z]{1}[0-9]{7}$", message = "Invalid Passport number format")
        @Size(min = 0) // Allows empty values
        private String passport = "";

        @Pattern(regexp = "^[A-Za-z0-9]{8,16}$", message = "Invalid Driving License format")
        @Size(min = 0) // Allows empty values
        private String drivingLicense = "";

        @Pattern(regexp = "^[A-Z]{3}[0-9]{7}$", message = "Invalid Voter ID format")
        @Size(min = 0) // Allows empty values
        private String voterId = "";
    }

    // Nested class for Bank Details
    @Getter
    @Setter
    public static class BankDetails {
        @Pattern(regexp = "\\d{9,18}", message = "Account number must be between 9 to 18 digits")
        @Size(min = 0) // Allows empty values
        private String accountNumber = "";

        private String accountHolderName = "";

        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC Code format")
        @Size(min = 0) // Allows empty values
        private String ifscCode = "";

        private String bankName = "";
        private String branchName = "";
    }

    // Nested class for Salary Details
    @Getter
    @Setter
    public static class SalaryDetails {
        private Double totalCtc = 0.0;
        private Double basic = 0.0;
        private Double allowances = 0.0;
        private Double hra = 0.0;
        private Double pf = 0.0;
    }
}
