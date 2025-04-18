package com.medhir.rest.employee;

import com.medhir.rest.auth.service.EmployeeAuthService;
import com.medhir.rest.dto.RegisterAdminRequest;
import com.medhir.rest.dto.UserCompanyDTO;
import com.medhir.rest.dto.CompanyEmployeeDTO;
import com.medhir.rest.dto.ManagerEmployeeDTO;
import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.model.ModuleModel;
import com.medhir.rest.repository.ModuleRepository;
import com.medhir.rest.service.CompanyService;
import com.medhir.rest.service.MinioService;
import com.medhir.rest.settings.designations.DesignationModel;
import com.medhir.rest.settings.designations.DesignationService;
import com.medhir.rest.settings.department.DepartmentService;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    @Value("${auth.service.url}")
    String authServiceUrl ;
    @Value("${attendance.service.url}")
    String attendanceServiceUrl;



    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private MinioService minioService;
    @Autowired
    private GeneratedId generatedId;
    @Autowired
    private CompanyService companyService;
    @Autowired
    ModuleRepository moduleRepository;
    @Autowired
    private DesignationService designationService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private EmployeeAuthService employeeAuthService;

    //Create Employee
    public EmployeeModel createEmployee(EmployeeModel employee,
                                        MultipartFile profileImage,
                                        MultipartFile aadharImage,
                                        MultipartFile panImage,
                                        MultipartFile passportImage,
                                        MultipartFile drivingLicenseImage,
                                        MultipartFile voterIdImage,
                                        MultipartFile passbookImage) {
        if(employeeRepository.findByEmployeeId(employee.getEmployeeId()).isPresent()){
            throw new DuplicateResourceException("Employee ID already exists: " + employee.getEmployeeId());
        }
        if(employee.getEmailPersonal() != null){
            if(employeeRepository.findByEmailPersonal(employee.getEmailPersonal()).isPresent()){
                throw new DuplicateResourceException("Email already exists: " + employee.getEmailPersonal());
            }
        }

        if(employeeRepository.findByPhone(employee.getPhone()).isPresent()){
            throw new DuplicateResourceException("Phone number already exists : " + employee.getPhone());
        }

        // Set role as EMPLOYEE
        employee.setRoles(Set.of("EMPLOYEE"));

        employee = setDefaultValues(employee);

        // Generate image URLs only after validation passes
        if (profileImage != null) {
            employee.setEmployeeImgUrl(minioService.uploadProfileImage(profileImage, employee.getEmployeeId()));
        }
        if (aadharImage != null) {
            employee.getIdProofs().setAadharImgUrl(minioService.uploadDocumentsImg(aadharImage, employee.getEmployeeId()));
        }
        if (panImage != null) {
            employee.getIdProofs().setPancardImgUrl(minioService.uploadDocumentsImg(panImage, employee.getEmployeeId()));
        }
        if (passportImage != null) {
            employee.getIdProofs().setPassportImgUrl(minioService.uploadDocumentsImg(passportImage, employee.getEmployeeId()));
        }
        if (drivingLicenseImage != null) {
            employee.getIdProofs().setDrivingLicenseImgUrl(minioService.uploadDocumentsImg(drivingLicenseImage, employee.getEmployeeId()));
        }
        if (voterIdImage != null) {
            employee.getIdProofs().setVoterIdImgUrl(minioService.uploadDocumentsImg(voterIdImage, employee.getEmployeeId()));
        }
        if (passbookImage != null) {
            employee.getBankDetails().setPassbookImgUrl(minioService.uploadDocumentsImg(passbookImage, employee.getEmployeeId()));
        }


        EmployeeModel savedEmployee = employeeRepository.save(employee);

        // Register employee for login with email
        employeeAuthService.registerEmployee(
            savedEmployee.getEmployeeId(),
            savedEmployee.getEmailPersonal(),
            savedEmployee.getPhone()
        );

        // call Attendance Service to register user for face verification
        registerUserInAttendanceService(savedEmployee);



        return savedEmployee;
    }

    // Get All Employees
    public List<EmployeeModel> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Get All Employees with minimal fields (name and employeeId)
    public List<Map<String, String>> getAllEmployeesMinimal() {
        List<EmployeeModel> employees = employeeRepository.findAll();
        return employees.stream()
            .map(employee -> Map.of(
                "name", employee.getName(),
                "employeeId", employee.getEmployeeId()
            ))
            .collect(Collectors.toList());
    }

    // Get All Employees by Company ID
    public List<EmployeeModel> getAllEmployeesByCompanyId(String companyId) {
        return employeeRepository.findByCompanyId(companyId);
    }


    // Get Employee By EmployeeId
    public Optional<EmployeeModel> getEmployeeById(String employeeId){
        return employeeRepository.findByEmployeeId(employeeId);
    }


    // Get Employees by Manager
    public List<ManagerEmployeeDTO> getEmployeesByManager(String managerId){
        List<EmployeeModel> employees = employeeRepository.findByReportingManager(managerId);
        
        return employees.stream()
            .map(employee -> {
                ManagerEmployeeDTO dto = new ManagerEmployeeDTO();
                dto.setEmployeeId(employee.getEmployeeId());
                dto.setName(employee.getName());
                dto.setFathersName(employee.getFathersName());
                dto.setPhone(employee.getPhone());
                dto.setEmailOfficial(employee.getEmailOfficial());
                dto.setJoiningDate(employee.getJoiningDate());
                dto.setCurrentAddress(employee.getCurrentAddress());
                
                // Get designation name from designation service
                try {
                    Optional<DesignationModel> designation = Optional.ofNullable(designationService.getDesignationById(employee.getDesignation()));
                    designation.ifPresent(d -> dto.setDesignationName(d.getName()));
                    if (designation.isEmpty()) {
                        dto.setDesignationName(employee.getDesignation());
                    }
                } catch (Exception e) {
                    dto.setDesignationName(employee.getDesignation());
                }
                
                return dto;
            })
            .collect(Collectors.toList());
    }

    // Delete Employee by Employee ID
    public void deleteEmployee(String employeeId) {
        Optional<EmployeeModel> employeeOpt = employeeRepository.findByEmployeeId(employeeId);
        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        
        // Delete the employee
        employeeRepository.delete(employeeOpt.get());
    }

    // Update Employee
    public EmployeeModel updateEmployee(String employeeId, EmployeeModel updatedEmployee,
                                        MultipartFile profileImage,
                                        MultipartFile aadharImage,
                                        MultipartFile panImage,
                                        MultipartFile passportImage,
                                        MultipartFile drivingLicenseImage,
                                        MultipartFile voterIdImage,
                                        MultipartFile passbookImage) {
        return employeeRepository.findByEmployeeId(employeeId).map(existingEmployee -> {

            Optional<EmployeeModel> employeeIDExists = employeeRepository.findByEmployeeId(updatedEmployee.getEmployeeId());
            if(employeeIDExists.isPresent() && !employeeIDExists.get().getEmployeeId().equals(employeeId)){
                throw new DuplicateResourceException("Employee ID already exists: " + updatedEmployee.getEmployeeId());
            }

            if (updatedEmployee.getEmailPersonal() != null) {
                Optional<EmployeeModel> emailExists = employeeRepository.findByEmailPersonal(updatedEmployee.getEmailPersonal());
                if (emailExists.isPresent() && !emailExists.get().getEmployeeId().equals(employeeId)) {
                    throw new DuplicateResourceException(emailExists.get().getEmailPersonal() + " : Email is already in use by another Employee");
                }
            }

            Optional<EmployeeModel> phoneExists = employeeRepository.findByPhone(updatedEmployee.getPhone());
            if (phoneExists.isPresent() && !phoneExists.get().getEmployeeId().equals(employeeId)) {
                throw new DuplicateResourceException(phoneExists.get().getPhone() + " : Phone number is already in use by another Employee");
            }

            // Update basic details
            existingEmployee.setName(updatedEmployee.getName());
            existingEmployee.setDesignation(updatedEmployee.getDesignation());
            existingEmployee.setFathersName(updatedEmployee.getFathersName());
            existingEmployee.setOvertimeEligibile(updatedEmployee.isOvertimeEligibile());
            existingEmployee.setPfEnrolled(updatedEmployee.isPfEnrolled());
            existingEmployee.setUanNumber(updatedEmployee.getUanNumber());
            existingEmployee.setEsicEnrolled(updatedEmployee.isEsicEnrolled());
            existingEmployee.setEsicNumber(updatedEmployee.getEsicNumber());
            existingEmployee.setWeeklyOffs(updatedEmployee.getWeeklyOffs());
            existingEmployee.setEmailPersonal(updatedEmployee.getEmailPersonal());
            existingEmployee.setEmailOfficial(updatedEmployee.getEmailOfficial());
            existingEmployee.setPhone(updatedEmployee.getPhone());
            existingEmployee.setAlternatePhone(updatedEmployee.getAlternatePhone());
            existingEmployee.setDepartment(updatedEmployee.getDepartment());
            existingEmployee.setGender(updatedEmployee.getGender());
            existingEmployee.setReportingManager(updatedEmployee.getReportingManager());
            existingEmployee.setPermanentAddress(updatedEmployee.getPermanentAddress());
            existingEmployee.setCurrentAddress(updatedEmployee.getCurrentAddress());
            existingEmployee.setSalaryDetails(updatedEmployee.getSalaryDetails());
            existingEmployee.setJoiningDate(updatedEmployee.getJoiningDate());

            // Update Bank Details
            if (updatedEmployee.getBankDetails() != null) {
                if (existingEmployee.getBankDetails() == null) {
                    existingEmployee.setBankDetails(new EmployeeModel.BankDetails());
                }
                existingEmployee.getBankDetails().setAccountNumber(updatedEmployee.getBankDetails().getAccountNumber());
                existingEmployee.getBankDetails().setAccountHolderName(updatedEmployee.getBankDetails().getAccountHolderName());
                existingEmployee.getBankDetails().setIfscCode(updatedEmployee.getBankDetails().getIfscCode());
                existingEmployee.getBankDetails().setBankName(updatedEmployee.getBankDetails().getBankName());
                existingEmployee.getBankDetails().setBranchName(updatedEmployee.getBankDetails().getBranchName());
                existingEmployee.getBankDetails().setUpiId(updatedEmployee.getBankDetails().getUpiId());
                existingEmployee.getBankDetails().setUpiPhoneNumber(updatedEmployee.getBankDetails().getUpiPhoneNumber());
            }

            // Update ID Proofs
            if (updatedEmployee.getIdProofs() != null) {
                if (existingEmployee.getIdProofs() == null) {
                    existingEmployee.setIdProofs(new EmployeeModel.IdProofs());
                }
                existingEmployee.getIdProofs().setAadharNo(updatedEmployee.getIdProofs().getAadharNo());
                existingEmployee.getIdProofs().setPanNo(updatedEmployee.getIdProofs().getPanNo());
                existingEmployee.getIdProofs().setPassport(updatedEmployee.getIdProofs().getPassport());
                existingEmployee.getIdProofs().setDrivingLicense(updatedEmployee.getIdProofs().getDrivingLicense());
                existingEmployee.getIdProofs().setVoterId(updatedEmployee.getIdProofs().getVoterId());
            }

            // Update Salary Details
            if (updatedEmployee.getSalaryDetails() != null) {
                if (existingEmployee.getSalaryDetails() == null) {
                    existingEmployee.setSalaryDetails(new EmployeeModel.SalaryDetails());
                }
                existingEmployee.getSalaryDetails().setAnnualCtc(updatedEmployee.getSalaryDetails().getAnnualCtc());
                existingEmployee.getSalaryDetails().setMonthlyCtc(updatedEmployee.getSalaryDetails().getMonthlyCtc());
                existingEmployee.getSalaryDetails().setBasicSalary(updatedEmployee.getSalaryDetails().getBasicSalary());
                existingEmployee.getSalaryDetails().setHra(updatedEmployee.getSalaryDetails().getHra());
                existingEmployee.getSalaryDetails().setAllowances(updatedEmployee.getSalaryDetails().getAllowances());
                existingEmployee.getSalaryDetails().setEmployerPfContribution(updatedEmployee.getSalaryDetails().getEmployerPfContribution());
                existingEmployee.getSalaryDetails().setEmployeePfContribution(updatedEmployee.getSalaryDetails().getEmployeePfContribution());
            }

            // Preserve existing images or update if a new image is uploaded
            if (profileImage != null) {
                existingEmployee.setEmployeeImgUrl(minioService.uploadProfileImage(profileImage, existingEmployee.getEmployeeId()));
            }

            if (existingEmployee.getIdProofs() == null) {
                existingEmployee.setIdProofs(new EmployeeModel.IdProofs());
            }

            if (aadharImage != null) {
                existingEmployee.getIdProofs().setAadharImgUrl(minioService.uploadDocumentsImg(aadharImage, existingEmployee.getEmployeeId()));
            }

            if (panImage != null) {
                existingEmployee.getIdProofs().setPancardImgUrl(minioService.uploadDocumentsImg(panImage, existingEmployee.getEmployeeId()));
            }

            if (passportImage != null) {
                existingEmployee.getIdProofs().setPassportImgUrl(minioService.uploadDocumentsImg(passportImage, existingEmployee.getEmployeeId()));
            }

            if (drivingLicenseImage != null) {
                existingEmployee.getIdProofs().setDrivingLicenseImgUrl(minioService.uploadDocumentsImg(drivingLicenseImage, existingEmployee.getEmployeeId()));
            }

            if (voterIdImage != null) {
                existingEmployee.getIdProofs().setVoterIdImgUrl(minioService.uploadDocumentsImg(voterIdImage, existingEmployee.getEmployeeId()));
            }

            if (existingEmployee.getBankDetails() == null) {
                existingEmployee.setBankDetails(new EmployeeModel.BankDetails());
            }

            if (passbookImage != null) {
                existingEmployee.getBankDetails().setPassbookImgUrl(minioService.uploadDocumentsImg(passbookImage, existingEmployee.getEmployeeId()));
            }

            existingEmployee = setDefaultValues(existingEmployee);
            //call Attendance Service to update user for face verification
            updateEmployeeInAttendanceService(existingEmployee);
            return employeeRepository.save(existingEmployee);
        }).orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));
    }



    // Set default values for missing fields
    private EmployeeModel setDefaultValues(EmployeeModel employee) {
        if (employee.getName() == null) employee.setName("");
        if (employee.getDesignation() == null) employee.setDesignation("");
        if (employee.getEmailPersonal() == null) employee.setEmailPersonal("");
        if (employee.getPhone() == null) employee.setPhone("");
        if (employee.getAlternatePhone() == null) employee.setAlternatePhone("");
        if (employee.getDepartment() == null) employee.setDepartment("");
        if (employee.getGender() == null) employee.setGender("");
        if (employee.getReportingManager() == null) employee.setReportingManager("");
        if (employee.getPermanentAddress() == null) employee.setPermanentAddress("");
        if (employee.getCurrentAddress() == null) employee.setCurrentAddress("");

        // ID Proofs
        if (employee.getIdProofs() == null) {
            employee.setIdProofs(new EmployeeModel.IdProofs());
        } else {
            if (employee.getIdProofs().getAadharNo() == null) employee.getIdProofs().setAadharNo("");
            if (employee.getIdProofs().getPanNo() == null) employee.getIdProofs().setPanNo("");
            if (employee.getIdProofs().getPassport() == null) employee.getIdProofs().setPassport("");
            if (employee.getIdProofs().getDrivingLicense() == null) employee.getIdProofs().setDrivingLicense("");
            if (employee.getIdProofs().getVoterId() == null) employee.getIdProofs().setVoterId("");
        }

        // Bank Details
        if (employee.getBankDetails() == null) {
            employee.setBankDetails(new EmployeeModel.BankDetails());
        } else {
            if (employee.getBankDetails().getAccountNumber() == null) employee.getBankDetails().setAccountNumber("");
            if (employee.getBankDetails().getAccountHolderName() == null) employee.getBankDetails().setAccountHolderName("");
            if (employee.getBankDetails().getIfscCode() == null) employee.getBankDetails().setIfscCode("");
            if (employee.getBankDetails().getBankName() == null) employee.getBankDetails().setBankName("");
            if (employee.getBankDetails().getBranchName() == null) employee.getBankDetails().setBranchName("");
            if (employee.getBankDetails().getUpiId() == null) employee.getBankDetails().setUpiId("");
            if (employee.getBankDetails().getUpiPhoneNumber() == null) employee.getBankDetails().setUpiPhoneNumber("");
        }

        // Salary Details
        if (employee.getSalaryDetails() == null) {
            employee.setSalaryDetails(new EmployeeModel.SalaryDetails());
        } else {
            if (employee.getSalaryDetails().getAnnualCtc() == null) employee.getSalaryDetails().setAnnualCtc(0.0);
            if (employee.getSalaryDetails().getMonthlyCtc() == null) employee.getSalaryDetails().setMonthlyCtc(0.0);
            if (employee.getSalaryDetails().getBasicSalary() == null) employee.getSalaryDetails().setBasicSalary(0.0);
            if (employee.getSalaryDetails().getHra() == null) employee.getSalaryDetails().setHra(0.0);
            if (employee.getSalaryDetails().getAllowances() == null) employee.getSalaryDetails().setAllowances(0.0);
            if (employee.getSalaryDetails().getEmployerPfContribution() == null) employee.getSalaryDetails().setEmployerPfContribution(0.0);
            if (employee.getSalaryDetails().getEmployeePfContribution() == null) employee.getSalaryDetails().setEmployeePfContribution(0.0);
        }

        return employee;
    }


    private void registerUserInAuthService(EmployeeModel employee) {

        Map<String, String> request = new HashMap<>();
        request.put("employeeId", employee.getEmployeeId());  // Assuming getId() returns employee ID
        request.put("username", employee.getName());  // Username = Employee Name
        request.put("password", employee.getName());  // Password = Employee Name

        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.postForEntity(authServiceUrl, request, String.class);
            System.out.println("User registered in Auth Service: " + employee.getName());
        } catch (Exception e) {
            System.err.println("Failed to register user in Auth Service: " + e.getMessage());
        }
    }


    private RestTemplate restTemplate = new RestTemplate();
    public void registerUserInAttendanceService(EmployeeModel employee) {
        try {
            // Create request parameters
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("employeeId", employee.getEmployeeId());
            requestBody.add("name", employee.getName());
            requestBody.add("imgUrl", employee.getEmployeeImgUrl()); // Always using imgUrl
            requestBody.add("joiningDate", employee.getJoiningDate().toString());

            // Set headers for form-data
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA); // Ensures compatibility with @RequestParam
            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Call the /register API
             restTemplate.postForEntity(attendanceServiceUrl + "/register", requestEntity, String.class);
            System.out.println("User registered in Attendance Service: " + employee.getName());

        } catch (Exception e) {
            System.err.println("Failed to register user in Attendance Service: " + e.getMessage());
        }
    }




    public void updateEmployeeInAttendanceService(EmployeeModel employee) {
        try {
            // Create request parameters
            MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
            requestBody.add("employeeId", employee.getEmployeeId());

            if (employee.getName() != null && !employee.getName().trim().isEmpty()) {
                requestBody.add("name", employee.getName());
            }

            if (employee.getEmployeeImgUrl() != null && !employee.getEmployeeImgUrl().trim().isEmpty()) {
                requestBody.add("imgUrl", employee.getEmployeeImgUrl());
            }

            if (employee.getJoiningDate() != null) {
                requestBody.add("joiningDate", employee.getJoiningDate().toString());
            }

            // Set headers for form-data
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // Make the PUT request
            ResponseEntity<String> response = restTemplate.exchange(
                    attendanceServiceUrl + "/update", HttpMethod.PUT, requestEntity, String.class);
            System.out.println("User Updated in Attendance Service: " + employee.getName());


        } catch (Exception e) {
            System.err.println("Failed to Update user in Attendance Service: " + e.getMessage());

        }
    }


    public String generateEmployeeId(String companyId) {
        CompanyModel company = companyService.getCompanyById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + companyId));

        String prefix = company.getPrefixForEmpID();

        return generatedId.generateId(prefix, EmployeeModel.class, "employeeId");
    }

    // Register Admin as Employee
    public EmployeeModel registerAdminAsEmployee(RegisterAdminRequest request) {
        // Create a new employee model
        EmployeeModel employee = new EmployeeModel();
        
        // Set basic details from the request
        employee.setName(request.getName());
        employee.setEmailPersonal(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setCompanyId(request.getCompanyId());
        
        // Set roles as both EMPLOYEE and HRADMIN
        employee.setRoles(Set.of("EMPLOYEE", "HRADMIN"));
        
        // Generate employee ID
        employee.setEmployeeId(generateEmployeeId(request.getCompanyId()));
        
        // Set default values for required fields
        employee = setDefaultValues(employee);
        
        // Save the employee
        EmployeeModel savedEmployee = employeeRepository.save(employee);

        // Register employee for login with email
        employeeAuthService.registerEmployee(
                savedEmployee.getEmployeeId(),
                savedEmployee.getEmailPersonal(),
                savedEmployee.getPhone()
        );
        
        // Register user in attendance service
        registerUserInAttendanceService(savedEmployee);
        
        return savedEmployee;
    }

    public List<UserCompanyDTO> getEmployeeCompanies(String employeeId) {
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + employeeId + " not found"));

        if (employee.getModuleIds() == null || employee.getModuleIds().isEmpty()) {
            return List.of();
        }

        // Get all modules for the employee
        List<ModuleModel> modules = employee.getModuleIds().stream()
                .map(moduleId -> moduleRepository.findByModuleId(moduleId))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();

        // Get unique company IDs from modules
        Set<String> companyIds = modules.stream()
                .filter(module -> module.getCompanyId() != null)
                .map(ModuleModel::getCompanyId)
                .collect(Collectors.toSet());

        // Get company details for each company ID
        return companyIds.stream()
                .map(companyId -> {
                    try {
                        Optional<CompanyModel> company = companyService.getCompanyById(companyId);
                        return new UserCompanyDTO(
                                company.get().getCompanyId(),
                                company.get().getName(),
                                company.get().getColorCode()
                        );
                    } catch (ResourceNotFoundException e) {
                        return new UserCompanyDTO(companyId, "Unknown Company", "Unknown Color");
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, String>> getManagersByDepartment(String departmentId) {
        // Get all designations in the department that have isManager=true
        List<DesignationModel> managerDesignations = designationService.getDesignationsByDepartment(departmentId)
            .stream()
            .filter(DesignationModel::isManager)
            .collect(Collectors.toList());

        // Get all designation IDs
        List<String> managerDesignationIds = managerDesignations.stream()
            .map(DesignationModel::getDesignationId)
            .collect(Collectors.toList());

        // Get all employees with these designations
        List<EmployeeModel> managers = employeeRepository.findByDepartmentAndDesignationIn(departmentId, managerDesignationIds);

        // Map to required format (name and employeeId)
        return managers.stream()
            .map(manager -> Map.of(
                "name", manager.getName(),
                "employeeId", manager.getEmployeeId()
            ))
            .collect(Collectors.toList());
    }

    // Get All Employees by Company ID with additional details
    public List<CompanyEmployeeDTO> getAllEmployeesByCompanyIdWithDetails(String companyId) {
        List<EmployeeModel> employees = employeeRepository.findByCompanyId(companyId);
        
        return employees.stream()
            .map(employee -> {
                CompanyEmployeeDTO dto = new CompanyEmployeeDTO(employee);
                
                // Get department name from department service
                try {
                    if (employee.getDepartment() != null && !employee.getDepartment().isEmpty()) {
                        dto.setDepartmentName(departmentService.getDepartmentById(employee.getDepartment()).getName());
                    }
                } catch (Exception e) {
                    dto.setDepartmentName(employee.getDepartment());
                }
                
                // Get designation name from designation service
                try {
                    Optional<DesignationModel> designation = Optional.ofNullable(designationService.getDesignationById(employee.getDesignation()));
                    designation.ifPresent(d -> dto.setDesignationName(d.getName()));
                    if (designation.isEmpty()) {
                        dto.setDesignationName(employee.getDesignation());
                    }
                } catch (Exception e) {
                    dto.setDesignationName(employee.getDesignation());
                }
                
                // Get reporting manager name
                if (employee.getReportingManager() != null && !employee.getReportingManager().isEmpty()) {
                    employeeRepository.findByEmployeeId(employee.getReportingManager())
                        .ifPresent(manager -> dto.setReportingManagerName(manager.getName()));
                }
                
                return dto;
            })
            .collect(Collectors.toList());
    }


    public EmployeeModel updateEmployeeRole(String employeeId, List<String> roles, String operation) {
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles list cannot be null or empty");
        }

        // Get the employee
        EmployeeModel employee = employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // Get current roles or initialize
        Set<String> currentRoles = Optional.ofNullable(employee.getRoles())
                .map(HashSet::new)
                .orElseGet(HashSet::new);

        // Normalize operation
        String op = operation == null ? "" : operation.trim().toUpperCase();

        switch (op) {
            case "ADD":
                currentRoles.addAll(roles);
                break;
            case "REMOVE":
                currentRoles.removeAll(roles);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation. Must be 'ADD' or 'REMOVE'");
        }

        employee.setRoles(currentRoles);
        return employeeRepository.save(employee);
    }

}
