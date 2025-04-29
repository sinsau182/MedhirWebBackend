package com.medhir.rest.employeeUpdateRequest;

import com.medhir.rest.employee.EmployeeModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hradmin")
public class HrUpdateController {

    @Autowired
    private EmployeeUpdateService employeeUpdateService;

    @GetMapping("/update-requests")
    public ResponseEntity<List<Map<String, Object>>> getAllUpdateRequests() {
        List<EmployeeModel> pendingRequests = employeeUpdateService.getAllPendingRequests();

        List<Map<String, Object>> detailedRequests = pendingRequests.stream()
                .map(employee -> {
                    List<UpdateFieldComparison> changes = compareFields(employee, employee.getPendingUpdateRequest());
                    return Map.of(
                            "employeeId", employee.getEmployeeId(),
                            "employeeName", employee.getName(),
                            "status", employee.getUpdateStatus(),
                            "changes", changes);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(detailedRequests);
    }

    private List<UpdateFieldComparison> compareFields(EmployeeModel employee, EmployeeUpdateRequest updateRequest) {
        List<UpdateFieldComparison> changes = new ArrayList<>();

        // Compare basic fields
        if (updateRequest.getEmailPersonal() != null
                && !updateRequest.getEmailPersonal().equals(employee.getEmailPersonal())) {
            changes.add(new UpdateFieldComparison("Personal Email",
                    employee.getEmailPersonal(), updateRequest.getEmailPersonal()));
        }

        if (updateRequest.getPhone() != null && !updateRequest.getPhone().equals(employee.getPhone())) {
            changes.add(new UpdateFieldComparison("Phone",
                    employee.getPhone(), updateRequest.getPhone()));
        }
        if (updateRequest.getAlternatePhone() != null
                && !updateRequest.getAlternatePhone().equals(employee.getAlternatePhone())) {
            changes.add(new UpdateFieldComparison("alternatePhone",
                    employee.getAlternatePhone(), updateRequest.getAlternatePhone()));
        }

        if (updateRequest.getCurrentAddress() != null
                && !updateRequest.getCurrentAddress().equals(employee.getCurrentAddress())) {
            changes.add(new UpdateFieldComparison("Current Address",
                    employee.getCurrentAddress(), updateRequest.getCurrentAddress()));
        }

        if (updateRequest.getPermanentAddress() != null
                && !updateRequest.getPermanentAddress().equals(employee.getPermanentAddress())) {
            changes.add(new UpdateFieldComparison("Permanent Address",
                    employee.getPermanentAddress(), updateRequest.getPermanentAddress()));
        }

        // Compare bank details
        if (employee.getBankDetails() != null) {
            if (updateRequest.getAccountHolderName() != null &&
                    !updateRequest.getAccountHolderName().equals(employee.getBankDetails().getAccountHolderName())) {
                changes.add(new UpdateFieldComparison("Account Holder Name",
                        employee.getBankDetails().getAccountHolderName(), updateRequest.getAccountHolderName()));
            }

            if (updateRequest.getAccountNumber() != null &&
                    !updateRequest.getAccountNumber().equals(employee.getBankDetails().getAccountNumber())) {
                changes.add(new UpdateFieldComparison("Account Number",
                        employee.getBankDetails().getAccountNumber(), updateRequest.getAccountNumber()));
            }

            if (updateRequest.getBankName() != null &&
                    !updateRequest.getBankName().equals(employee.getBankDetails().getBankName())) {
                changes.add(new UpdateFieldComparison("Bank Name",
                        employee.getBankDetails().getBankName(), updateRequest.getBankName()));
            }

            if (updateRequest.getBranchName() != null &&
                    !updateRequest.getBranchName().equals(employee.getBankDetails().getBranchName())) {
                changes.add(new UpdateFieldComparison("Branch Name",
                        employee.getBankDetails().getBranchName(), updateRequest.getBranchName()));
            }

            if (updateRequest.getIfscCode() != null &&
                    !updateRequest.getIfscCode().equals(employee.getBankDetails().getIfscCode())) {
                changes.add(new UpdateFieldComparison("IFSC Code",
                        employee.getBankDetails().getIfscCode(), updateRequest.getIfscCode()));
            }

            if (updateRequest.getUpiId() != null &&
                    !updateRequest.getUpiId().equals(employee.getBankDetails().getUpiId())) {
                changes.add(new UpdateFieldComparison("UPI ID",
                        employee.getBankDetails().getUpiId(), updateRequest.getUpiId()));
            }

            if (updateRequest.getUpiPhoneNumber() != null &&
                    !updateRequest.getUpiPhoneNumber().equals(employee.getBankDetails().getUpiPhoneNumber())) {
                changes.add(new UpdateFieldComparison("UPI Phone Number",
                        employee.getBankDetails().getUpiPhoneNumber(), updateRequest.getUpiPhoneNumber()));
            }
        }

        // Check for image updates
        if (updateRequest.getProfileImgUrl() != null) {
            changes.add(new UpdateFieldComparison("Profile Image",
                    employee.getEmployeeImgUrl(), updateRequest.getProfileImgUrl()));
        }

        if (updateRequest.getAadharImgUrl() != null) {
            changes.add(new UpdateFieldComparison("Aadhar Card Image",
                    employee.getIdProofs() != null ? employee.getIdProofs().getAadharImgUrl() : "No previous image",
                    updateRequest.getAadharImgUrl()));
        }

        if (updateRequest.getPancardImgUrl() != null) {
            changes.add(new UpdateFieldComparison("PAN Card Image",
                    employee.getIdProofs() != null ? employee.getIdProofs().getPancardImgUrl() : "No previous image",
                    updateRequest.getPancardImgUrl()));
        }

        if (updateRequest.getPassportImgUrl() != null) {
            changes.add(new UpdateFieldComparison("Passport Image",
                    employee.getIdProofs() != null ? employee.getIdProofs().getPassportImgUrl() : "No previous image",
                    updateRequest.getPassportImgUrl()));
        }

        if (updateRequest.getDrivingLicenseImgUrl() != null) {
            changes.add(new UpdateFieldComparison("Driving License Image",
                    employee.getIdProofs() != null ? employee.getIdProofs().getDrivingLicenseImgUrl()
                            : "No previous image",
                    updateRequest.getDrivingLicenseImgUrl()));
        }

        if (updateRequest.getVoterIdImgUrl() != null) {
            changes.add(new UpdateFieldComparison("Voter ID Image",
                    employee.getIdProofs() != null ? employee.getIdProofs().getVoterIdImgUrl() : "No previous image",
                    updateRequest.getVoterIdImgUrl()));
        }

        if (updateRequest.getPassbookImgUrl() != null) {
            changes.add(new UpdateFieldComparison("Passbook Image",
                    employee.getBankDetails() != null ? employee.getBankDetails().getPassbookImgUrl()
                            : "No previous image",
                    updateRequest.getPassbookImgUrl()));
        }
        return changes;
    }

    @PutMapping("/update-requests/{employeeId}")
    public ResponseEntity<Map<String, Object>> approveOrRejectUpdate(
            @PathVariable String employeeId,
            @RequestParam String status) {

        if (!status.equals("Approved") && !status.equals("Rejected")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid status"));
        }

        boolean result = employeeUpdateService.processUpdateRequest(employeeId, status);

        if (!result) {
            return ResponseEntity.badRequest().body(Map.of("error", "Request not found or already processed"));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Update request processed successfully"));
    }
}
