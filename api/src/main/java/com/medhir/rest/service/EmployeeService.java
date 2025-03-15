package com.medhir.rest.service;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.model.EmployeeModel;
import com.medhir.rest.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    //Create Employee
    public EmployeeModel createEmployee(EmployeeModel employee) {

        if(employee.getEmail() != null){
            if(employeeRepository.findByEmail(employee.getEmail()).isPresent()){
                throw new DuplicateResourceException("Email already exists: " + employee.getEmail());
            }
        }

        if(employeeRepository.findByPhone(employee.getPhone()).isPresent()){
            throw new DuplicateResourceException("Phone number already exists : " + employee.getPhone());
        }
        employee = setDefaultValues(employee);
        return employeeRepository.save(employee);
    }

    // Get All Employees
    public List<EmployeeModel> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // Update Employee
    public EmployeeModel updateEmployee(String id, EmployeeModel updatedEmployee) {
        return employeeRepository.findById(id).map(existingEmployee -> {

            if(updatedEmployee.getEmail() != null){
                Optional<EmployeeModel> emailExists = employeeRepository.findByEmail(updatedEmployee.getEmail());
                if(emailExists.isPresent() && !emailExists.get().getId().equals(id)){
                    throw new DuplicateResourceException(emailExists.get().getEmail() +" : Email is already in use by other Employee");
                }
            }


            Optional<EmployeeModel> phoneExists = employeeRepository.findByPhone(updatedEmployee.getPhone());
            if(phoneExists.isPresent() && !phoneExists.get().getId().equals(id)){
                throw new DuplicateResourceException(phoneExists.get().getPhone() +" : Phone number is already in use by other Employee");
            }


            existingEmployee.setName(updatedEmployee.getName());
            existingEmployee.setTitle(updatedEmployee.getTitle());
            existingEmployee.setEmail(updatedEmployee.getEmail());
            existingEmployee.setPhone(updatedEmployee.getPhone());
            existingEmployee.setDepartment(updatedEmployee.getDepartment());
            existingEmployee.setGender(updatedEmployee.getGender());
            existingEmployee.setReportingManager(updatedEmployee.getReportingManager());
            existingEmployee.setPermanentAddress(updatedEmployee.getPermanentAddress());
            existingEmployee.setCurrentAddress(updatedEmployee.getCurrentAddress());
            existingEmployee.setIdProofs(updatedEmployee.getIdProofs());
            existingEmployee.setBankDetails(updatedEmployee.getBankDetails());
            existingEmployee.setSalaryDetails(updatedEmployee.getSalaryDetails());
            return employeeRepository.save(existingEmployee);
        }).orElseThrow(() -> new ResourceNotFoundException("Employee with ID " + id + " not found"));
    }


    // Set default values for missing fields
    private EmployeeModel setDefaultValues(EmployeeModel employee) {
        if (employee.getName() == null) employee.setName("");
        if (employee.getTitle() == null) employee.setTitle("");
        if (employee.getEmail() == null) employee.setEmail("");
        if (employee.getPhone() == null) employee.setPhone("");
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
        }

        // Salary Details
        if (employee.getSalaryDetails() == null) {
            employee.setSalaryDetails(new EmployeeModel.SalaryDetails());
        } else {
            if (employee.getSalaryDetails().getTotalCtc() == null) employee.getSalaryDetails().setTotalCtc(0.0);
            if (employee.getSalaryDetails().getBasic() == null) employee.getSalaryDetails().setBasic(0.0);
            if (employee.getSalaryDetails().getAllowances() == null) employee.getSalaryDetails().setAllowances(0.0);
            if (employee.getSalaryDetails().getHra() == null) employee.getSalaryDetails().setHra(0.0);
            if (employee.getSalaryDetails().getPf() == null) employee.getSalaryDetails().setPf(0.0);
        }

        return employee;
    }

}
