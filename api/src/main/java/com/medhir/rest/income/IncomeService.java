package com.medhir.rest.income;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;
import com.medhir.rest.employee.EmployeeModel;
import com.medhir.rest.employee.EmployeeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GeneratedId generatedId;

    public Income createIncome(Income income) {
        try {
            // Validate submittedBy
            if (!StringUtils.hasText(income.getSubmittedBy())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submitted by (employeeId) is required");
            }

            income.setGeneratedId(generatedId);
            income.generateIncomeId();
            return incomeRepository.save(income);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating income: " + e.getMessage());
        }
    }

    public List<Income> getAllIncomes() {
        return incomeRepository.findAll();
    }

    public List<Income> getIncomesByEmployee(String employeeId) {
        if (!StringUtils.hasText(employeeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID is required");
        }
        return incomeRepository.findBySubmittedBy(employeeId);
    }

    public List<Income> getIncomesByManagerAndStatus(String managerId, String status) {
        if (!StringUtils.hasText(managerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Manager ID is required");
        }
        if (!StringUtils.hasText(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        // Find all employees who have this manager as their reporting manager
        List<EmployeeModel> employees = employeeRepository.findByReportingManager(managerId);
        
        // If no employees found, return empty list instead of throwing error
        if (employees.isEmpty()) {
            return new ArrayList<>();
        }

        // Get all employee IDs
        List<String> employeeIds = employees.stream()
            .map(EmployeeModel::getEmployeeId)
            .collect(Collectors.toList());

        return incomeRepository.findBySubmittedByInAndStatusOrderBySubmittedBy(employeeIds, status);
    }

    public List<Income> getIncomesByCompanyAndStatus(String companyId, String status) {
        if (!StringUtils.hasText(companyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company ID is required");
        }
        if (!StringUtils.hasText(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        return incomeRepository.findByCompanyIdAndStatusOrderBySubmittedBy(companyId, status);
    }

    public Optional<Income> getIncomeById(String incomeId) {
        return incomeRepository.findByIncomeId(incomeId);
    }


    public Income updateIncome(String incomeId, Income income) {
        Optional<Income> existingIncome = incomeRepository.findByIncomeId(incomeId);
        if (existingIncome.isPresent()) {
            Income currentIncome = existingIncome.get();

            // Check if the expense status is Pending
            if (!"Pending".equals(currentIncome.getStatus())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Cannot update income. Only incomes with 'Pending' status can be updated.");
            }
            
            // Create a new income object with existing values
            Income updatedIncome = new Income();
            updatedIncome.setId(currentIncome.getId());
            updatedIncome.setIncomeId(currentIncome.getIncomeId());
            
            // Copy all existing values
            updatedIncome.setProject(currentIncome.getProject());
            updatedIncome.setClient(currentIncome.getClient());
            updatedIncome.setAmount(currentIncome.getAmount());
            updatedIncome.setInitiated(currentIncome.getInitiated());
            updatedIncome.setStatus(currentIncome.getStatus());
            updatedIncome.setFile(currentIncome.getFile());
            updatedIncome.setComments(currentIncome.getComments());
            updatedIncome.setSubmittedBy(currentIncome.getSubmittedBy());
            updatedIncome.setCompanyId(currentIncome.getCompanyId());
            
            // Update only the fields that are present in the request
            if (income.getProject() != null) updatedIncome.setProject(income.getProject());
            if (income.getClient() != null) updatedIncome.setClient(income.getClient());
            if (income.getAmount() != null) updatedIncome.setAmount(income.getAmount());
            // if (income.getInitiated() != null) updatedIncome.setInitiated(income.getInitiated());
            // if (income.getStatus() != null) updatedIncome.setStatus(income.getStatus());
            if (income.getFile() != "") updatedIncome.setFile(income.getFile());
            if (income.getComments() != "") updatedIncome.setComments(income.getComments());
            return incomeRepository.save(updatedIncome);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Income with ID '" + incomeId + "' not found");
        }
    }

    public void deleteIncome(String incomeId) {
        Optional<Income> income = incomeRepository.findByIncomeId(incomeId);
        if (income.isPresent()) {
            try {
                incomeRepository.delete(income.get());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting income: " + e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Income with ID '" + incomeId + "' not found");
        }
    }

    public Income updateIncomeStatusByHrAdmin(String incomeId, String status, String remarks) {
        Optional<Income> existingIncome = incomeRepository.findByIncomeId(incomeId);
        if (existingIncome.isPresent()) {
            Income income = existingIncome.get();
            
            // Validate status
            if (!StringUtils.hasText(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
            }
            
            // Update status and status remarks
            income.setStatus(status);
            if (remarks != null) {
                income.setStatusRemarks(remarks);
            }
            
            return incomeRepository.save(income);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Income with ID '" + incomeId + "' not found");
        }
    }

    public Income updateIncomeStatusByManager(String incomeId, String status, String remarks, String managerId) {
        Optional<Income> existingIncome = incomeRepository.findByIncomeId(incomeId);
        if (existingIncome.isPresent()) {
            Income income = existingIncome.get();
            
            // Validate status
            if (!StringUtils.hasText(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
            }

            // Check if manager can update this expense
            if (!canManagerUpdateIncome(managerId, income)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "You don't have permission to update this income. You can only update incomes of your team members.");
            }
            
            // Update status and status remarks
            income.setStatus(status);
            if (remarks != null) {
                income.setStatusRemarks(remarks);
            }
            
            return incomeRepository.save(income);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Income with ID '" + incomeId + "' not found");
        }
    }

    // Keep the existing updateIncomeStatus method for backward compatibility
    // public Income updateIncomeStatus(String incomeId, String status, String remarks, String currentUserRole, String currentUserId) {
    //     if ("HRADMIN".equals(currentUserRole)) {
    //         return updateIncomeStatusByHrAdmin(incomeId, status, remarks);
    //     } else if ("MANAGER".equals(currentUserRole)) {
    //         return updateIncomeStatusByManager(incomeId, status, remarks, currentUserId);
    //     } else {
    //         throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HRADMIN and MANAGER roles can update income status");
    //     }
    // }

    private boolean canManagerUpdateIncome(String managerId, Income income) {
        // First check if the manager exists and has the MANAGER role
        Optional<EmployeeModel> managerOpt = employeeRepository.findByEmployeeId(managerId);
        if (managerOpt.isEmpty() || !managerOpt.get().getRoles().contains("MANAGER")) {
            return false;
        }

        // Find the employee who submitted the expense
        Optional<EmployeeModel> employeeOpt = employeeRepository.findByEmployeeId(income.getSubmittedBy());
        if (employeeOpt.isEmpty()) {
            return false;
        }

        // Check if the manager is the reporting manager of the employee
        return managerId.equals(employeeOpt.get().getReportingManager());
    }
    
} 