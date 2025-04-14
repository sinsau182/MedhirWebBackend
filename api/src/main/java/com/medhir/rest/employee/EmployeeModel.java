package com.medhir.rest.employee;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.medhir.rest.employeeUpdateRequest.EmployeeUpdateRequest;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Document(collection = "employees")
@JsonInclude(JsonInclude.Include.NON_NULL) // Exclude null fields from JSON response
public class EmployeeModel {

    @Id
    private String id;

    @NotBlank(message = "Employee Id cannot be empty")
    @Indexed(unique = true)
    private String employeeId;
    
    @NotBlank(message = "Company Id cannot be empty")
    private String companyId;

    @NotBlank(message = "Employee name cannot be empty")
    private String name;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    @Indexed(unique = true)
    @NotBlank(message = "Phone number cannot be empty")
    private String phone;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String alternatePhone;

    private Set<String> Roles;
    private List<String> moduleIds;


    @Email(message = "Invalid email format!")
    @Indexed(unique = true)
    private String emailPersonal;

    @Email(message = "Invalid email format!")
    @Indexed(unique = true)
    private String emailOfficial;

    private String designation;
    private String fathersName;
    private boolean overtimeEligibile;
    private boolean pfEnrolled;
    private String uanNumber;
    private boolean esicEnrolled;
    private String esicNumber;
    private List<String> weeklyOffs;


    private String employeeImgUrl="";

    private LocalDate joiningDate;
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

    private String updateStatus ; //Approved, Pending, Rejected

    @Valid
    private EmployeeUpdateRequest pendingUpdateRequest; // Reference to EmployeeUpdateRequest which stores the pending updates

    // Nested class for ID Proofs
    @Getter
    @Setter
    public static class IdProofs {
        @Pattern(regexp = "\\d{12}", message = "Aadhar number must be exactly 12 digits")
        @Size(min = 0) // Allows empty values
        private String aadharNo = "";
        private String aadharImgUrl="";

        @Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN number format")
        @Size(min = 0) // Allows empty values
        private String panNo = "";
        private String pancardImgUrl="";

        @Pattern(regexp = "^[A-Z]{1}[0-9]{7}$", message = "Invalid Passport number format")
        @Size(min = 0) // Allows empty values
        private String passport = "";
        private String passportImgUrl="";

        @Pattern(regexp = "^[A-Za-z0-9]{8,16}$", message = "Invalid Driving License format")
        @Size(min = 0) // Allows empty values
        private String drivingLicense = "";
        private String drivingLicenseImgUrl="";

        @Pattern(regexp = "^[A-Z]{3}[0-9]{7}$", message = "Invalid Voter ID format")
        @Size(min = 0) // Allows empty values
        private String voterId = "";
        private String voterIdImgUrl="";
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
        private String upiId = "";
        private String upiPhoneNumber = "";

        private String passbookImgUrl;
    }

    // Nested class for Salary Details
    @Getter
    @Setter
    public static class SalaryDetails {
        private Double annualCtc = 0.0;
        private Double monthlyCtc = 0.0;
        private Double basicSalary = 0.0;
        private Double hra = 0.0;
        private Double allowances = 0.0;
        private Double employerPfContribution = 0.0;
        private Double employeePfContribution = 0.0;
    }
}
