package com.medhir.rest.employeeUpdateRequest;

import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeRepository;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeUpdateService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MinioService minioService;

    // Create update request with images
    public EmployeeModel createUpdateRequest(EmployeeUpdateRequest request,
            MultipartFile profileImage, MultipartFile aadharImage,
            MultipartFile panImage, MultipartFile passportImage,
            MultipartFile drivingLicenseImage, MultipartFile voterIdImage,
            MultipartFile passbookImage) {

        String employeeId = request.getEmployeeId();

        Optional<EmployeeModel> employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found: " + employeeId);
        }

        if (request.getEmailPersonal() != null) {
            Optional<EmployeeModel> emailExists = employeeRepository.findByEmailPersonal(request.getEmailPersonal());

            if (emailExists.isPresent() && !emailExists.get().getEmployeeId().equals(request.getEmployeeId())) {
                throw new DuplicateResourceException(
                        emailExists.get().getEmailPersonal() + " : Email is already in use by other Employee");
            }
        }

        Optional<EmployeeModel> phoneExists = employeeRepository.findByPhone(request.getPhone());
        if (phoneExists.isPresent() && !phoneExists.get().getEmployeeId().equals(request.getEmployeeId())) {
            throw new DuplicateResourceException(
                    phoneExists.get().getPhone() + " : Phone number is already in use by other Employee");
        }

        // Store images in MinIO and update URLs in request object
        if (profileImage != null) {
            request.setProfileImgUrl(minioService.uploadProfileImage(profileImage, employeeId));
        }
        if (aadharImage != null) {
            request.setAadharImgUrl(minioService.uploadDocumentsImg(aadharImage, employeeId));
        }
        if (panImage != null) {
            request.setPancardImgUrl(minioService.uploadDocumentsImg(panImage, employeeId));
        }
        if (passportImage != null) {
            request.setPassportImgUrl(minioService.uploadDocumentsImg(passportImage, employeeId));
        }
        if (drivingLicenseImage != null) {
            request.setDrivingLicenseImgUrl(minioService.uploadDocumentsImg(drivingLicenseImage, employeeId));
        }
        if (voterIdImage != null) {
            request.setVoterIdImgUrl(minioService.uploadDocumentsImg(voterIdImage, employeeId));
        }
        if (passbookImage != null) {
            request.setPassbookImgUrl(minioService.uploadDocumentsImg(passbookImage, employeeId));
        }

        // store the request in the employee Model in the Pending request field with
        // status as Pending
        EmployeeModel employeeModel = employee.get();
        employeeModel.setUpdateStatus("Pending");
        employeeModel.setPendingUpdateRequest(request);

        return employeeRepository.save(employeeModel);
    }

    public List<EmployeeModel> getAllPendingRequests() {
        return employeeRepository.findByUpdateStatus("Pending");
    }

    public List<EmployeeModel> getPendingRequestsByCompanyId(String companyId) {
        return employeeRepository.findByCompanyIdAndUpdateStatus(companyId, "Pending");
    }

    public List<EmployeeModel> getPendingRequestsByManagerId(String managerId) {
        return employeeRepository.findByReportingManagerAndUpdateStatus(managerId, "Pending");
    }

    public boolean processUpdateRequest(String employeeId, String status) {
        Optional<EmployeeModel> requestOpt = employeeRepository.findByEmployeeId(employeeId);
        if (requestOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found: " + employeeId);
        }

        EmployeeModel employee = requestOpt.get();

        if (!"Pending".equals(employee.getUpdateStatus()))
            return false; // Prevent re-processing

        // Get pending updates
        EmployeeUpdateRequest request = employee.getPendingUpdateRequest();
        if (request == null) { // No, pending update found
            throw new ResourceNotFoundException("No pending update request found for employee: " + employeeId);

        }
        if ("Approved".equals(status)) {

            // Apply the approved changes
            if (request.getEmailPersonal() != null)
                employee.setEmailPersonal(request.getEmailPersonal());
            if (request.getPhone() != null)
                employee.setPhone(request.getPhone());
            if (request.getAlternatePhone() != null)
                employee.setAlternatePhone(request.getAlternatePhone());
            if (request.getCurrentAddress() != null)
                employee.setCurrentAddress(request.getCurrentAddress());
            if (request.getPermanentAddress() != null)
                employee.setPermanentAddress(request.getPermanentAddress());
            if (request.getProfileImgUrl() != null)
                employee.setEmployeeImgUrl(request.getProfileImgUrl());

            // Bank Details
            if (employee.getBankDetails() != null) {
                if (request.getAccountHolderName() != null)
                    employee.getBankDetails().setAccountHolderName(request.getAccountHolderName());
                if (request.getAccountNumber() != null)
                    employee.getBankDetails().setAccountNumber(request.getAccountNumber());
                if (request.getBankName() != null)
                    employee.getBankDetails().setBankName(request.getBankName());
                if (request.getBranchName() != null)
                    employee.getBankDetails().setBranchName(request.getBranchName());
                if (request.getIfscCode() != null)
                    employee.getBankDetails().setIfscCode(request.getIfscCode());
                if (request.getPassbookImgUrl() != null)
                    employee.getBankDetails().setPassbookImgUrl(request.getPassbookImgUrl());
                if (request.getUpiId() != null)
                    employee.getBankDetails().setUpiId(request.getUpiId());
                if (request.getUpiPhoneNumber() != null)
                    employee.getBankDetails().setUpiPhoneNumber(request.getUpiPhoneNumber());
            }

            // ID Proofs
            if (employee.getIdProofs() != null) {
                if (request.getAadharImgUrl() != null)
                    employee.getIdProofs().setAadharImgUrl(request.getAadharImgUrl());
                if (request.getPancardImgUrl() != null)
                    employee.getIdProofs().setPancardImgUrl(request.getPancardImgUrl());
                if (request.getDrivingLicenseImgUrl() != null)
                    employee.getIdProofs().setDrivingLicenseImgUrl(request.getDrivingLicenseImgUrl());
                if (request.getVoterIdImgUrl() != null)
                    employee.getIdProofs().setVoterIdImgUrl(request.getVoterIdImgUrl());
                if (request.getPassportImgUrl() != null)
                    employee.getIdProofs().setPassportImgUrl(request.getPassportImgUrl());
            }

            // Clear pending request after approval
            employee.setPendingUpdateRequest(null);
            employee.setUpdateStatus("Approved");
        } else {
            // If rejected, just update the status without applying changes
            employee.setUpdateStatus("Rejected");
        }

        employeeRepository.save(employee);
        return true;
    }

}
