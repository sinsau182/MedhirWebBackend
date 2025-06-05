package com.medhir.rest.expenses;

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
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private GeneratedId generatedId;

    public Expense createExpense(Expense expense) {
        try {
            // Validate submittedBy
            if (!StringUtils.hasText(expense.getSubmittedBy())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submitted by (employeeId) is required");
            }

            expense.setGeneratedId(generatedId);
            expense.generateExpenseId();
            return expenseRepository.save(expense);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating expense: " + e.getMessage());
        }
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByEmployee(String employeeId) {
        if (!StringUtils.hasText(employeeId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee ID is required");
        }
        return expenseRepository.findBySubmittedBy(employeeId);
    }

    public List<Expense> getExpensesByManagerAndStatus(String managerId, String status) {
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

        // Get expenses for all these employees with the specified status
        return expenseRepository.findBySubmittedByInAndStatusOrderBySubmittedBy(employeeIds, status);
    }

    public List<Expense> getExpensesByCompanyAndStatus(String companyId, String status) {
        if (!StringUtils.hasText(companyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company ID is required");
        }
        if (!StringUtils.hasText(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        return expenseRepository.findByCompanyIdAndStatusOrderBySubmittedBy(companyId, status);
    }

    public Optional<Expense> getExpenseById(String expenseId) {
        return expenseRepository.findByExpenseId(expenseId);
    }

    public Expense updateExpense(String expenseId, Expense expense) {
        Optional<Expense> existingExpense = expenseRepository.findByExpenseId(expenseId);
        if (existingExpense.isPresent()) {
            Expense currentExpense = existingExpense.get();
            
            // Check if the expense status is Pending
            if (!"Pending".equals(currentExpense.getStatus())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "Cannot update expense. Only expenses with 'Pending' status can be updated.");
            }
            
            // Create a new expense object with existing values
            Expense updatedExpense = new Expense();
            updatedExpense.setId(currentExpense.getId());
            updatedExpense.setExpenseId(currentExpense.getExpenseId());
            
            // Copy all existing values
            updatedExpense.setMainHead(currentExpense.getMainHead());
            updatedExpense.setExpenseHead(currentExpense.getExpenseHead());
            updatedExpense.setVendor(currentExpense.getVendor());
            updatedExpense.setInitiated(currentExpense.getInitiated());
            updatedExpense.setStatus(currentExpense.getStatus());
            updatedExpense.setCategory(currentExpense.getCategory());
            updatedExpense.setGstCredit(currentExpense.getGstCredit());
            updatedExpense.setFile(currentExpense.getFile());
            updatedExpense.setTotalAmount(currentExpense.getTotalAmount());
            updatedExpense.setAmountRequested(currentExpense.getAmountRequested());
            updatedExpense.setComments(currentExpense.getComments());
            updatedExpense.setSubmittedBy(currentExpense.getSubmittedBy()); // Always keep original submittedBy
            updatedExpense.setCompanyId(currentExpense.getCompanyId());
            
            // Update only the fields that are present in the request (except submittedBy)
            if (expense.getMainHead() != null) updatedExpense.setMainHead(expense.getMainHead());
            if (expense.getExpenseHead() != null) updatedExpense.setExpenseHead(expense.getExpenseHead());
            if (expense.getVendor() != null) updatedExpense.setVendor(expense.getVendor());
            // Prevent changes to initiated and status fields
            // if (expense.getInitiated() != null) updatedExpense.setInitiated(expense.getInitiated());
            // if (expense.getStatus() != null) updatedExpense.setStatus(expense.getStatus());
            if (expense.getCategory() != "") updatedExpense.setCategory(expense.getCategory());
            if (expense.getGstCredit() != "") updatedExpense.setGstCredit(expense.getGstCredit());
            if (expense.getFile() != "") updatedExpense.setFile(expense.getFile());
            if (expense.getTotalAmount() != null) updatedExpense.setTotalAmount(expense.getTotalAmount());
            if (expense.getAmountRequested() != null) updatedExpense.setAmountRequested(expense.getAmountRequested());
            if (expense.getComments() != "") updatedExpense.setComments(expense.getComments());
            
            return expenseRepository.save(updatedExpense);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense with ID '" + expenseId + "' not found");
        }
    }

    public void deleteExpense(String expenseId) {
        Optional<Expense> expense = expenseRepository.findByExpenseId(expenseId);
        if (expense.isPresent()) {
            try {
                expenseRepository.delete(expense.get());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting expense: " + e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense with ID '" + expenseId + "' not found");
        }
    }

    public Expense updateExpenseStatusByHrAdmin(String expenseId, String status, String remarks) {
        Optional<Expense> existingExpense = expenseRepository.findByExpenseId(expenseId);
        if (existingExpense.isPresent()) {
            Expense expense = existingExpense.get();
            
            // Validate status
            if (!StringUtils.hasText(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
            }
            
            // Update status and status remarks
            expense.setStatus(status);
            if (remarks != null) {
                expense.setStatusRemarks(remarks);
            }
            
            return expenseRepository.save(expense);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense with ID '" + expenseId + "' not found");
        }
    }

    public Expense updateExpenseStatusByManager(String expenseId, String status, String remarks, String managerId) {
        Optional<Expense> existingExpense = expenseRepository.findByExpenseId(expenseId);
        if (existingExpense.isPresent()) {
            Expense expense = existingExpense.get();
            
            // Validate status
            if (!StringUtils.hasText(status)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
            }

            // Check if manager can update this expense
            if (!canManagerUpdateExpense(managerId, expense)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                    "You don't have permission to update this expense. You can only update expenses of your team members.");
            }
            
            // Update status and status remarks
            expense.setStatus(status);
            if (remarks != null) {
                expense.setStatusRemarks(remarks);
            }
            
            return expenseRepository.save(expense);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expense with ID '" + expenseId + "' not found");
        }
    }

    // Keep the existing updateExpenseStatus method for backward compatibility
    // public Expense updateExpenseStatus(String expenseId, String status, String remarks, String currentUserRole, String currentUserId) {
    //     if ("HRADMIN".equals(currentUserRole)) {
    //         return updateExpenseStatusByHrAdmin(expenseId, status, remarks);
    //     } else if ("MANAGER".equals(currentUserRole)) {
    //         return updateExpenseStatusByManager(expenseId, status, remarks, currentUserId);
    //     } else {
    //         throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HRADMIN and MANAGER roles can update expense status");
    //     }
    // }

    private boolean canManagerUpdateExpense(String managerId, Expense expense) {
        // First check if the manager exists and has the MANAGER role
        Optional<EmployeeModel> managerOpt = employeeRepository.findByEmployeeId(managerId);
        if (managerOpt.isEmpty() || !managerOpt.get().getRoles().contains("MANAGER")) {
            return false;
        }

        // Find the employee who submitted the expense
        Optional<EmployeeModel> employeeOpt = employeeRepository.findByEmployeeId(expense.getSubmittedBy());
        if (employeeOpt.isEmpty()) {
            return false;
        }

        // Check if the manager is the reporting manager of the employee
        return managerId.equals(employeeOpt.get().getReportingManager());
    }
}