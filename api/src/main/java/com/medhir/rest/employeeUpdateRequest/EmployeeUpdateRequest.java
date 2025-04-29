package com.medhir.rest.employeeUpdateRequest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeUpdateRequest {

    @NotBlank(message = "Employee Id cannot be empty")
    private String employeeId; // Reference to Employee

    private String emailPersonal;
    private String profileImgUrl;

    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phone;

    @Pattern(regexp = "\\d{10}", message = "Alternate Phone number must be exactly 10 digits")
    private String alternatePhone;

    private String currentAddress;
    private String permanentAddress;

    private String accountHolderName;

    @Pattern(regexp = "\\d{9,18}", message = "Account number must be between 9 to 18 digits")
    private String accountNumber;

    private String bankName;
    private String branchName;

    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC Code format")
    private String ifscCode;

    private String upiId;
    private String upiPhoneNumber;

    private String passbookImgUrl;
    private String aadharImgUrl;
    private String pancardImgUrl;
    private String drivingLicenseImgUrl;
    private String voterIdImgUrl;
    private String passportImgUrl;

}
