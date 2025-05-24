package com.medhir.rest.expenses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expenseRepository;

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

    public Optional<Expense> getExpenseById(String expenseId) {
        return expenseRepository.findByExpenseId(expenseId);
    }

    public Expense updateExpense(String expenseId, Expense expense) {
        Optional<Expense> existingExpense = expenseRepository.findByExpenseId(expenseId);
        if (existingExpense.isPresent()) {
            Expense currentExpense = existingExpense.get();
            
            // Verify submittedBy is present but prevent it from being changed
            if (!StringUtils.hasText(expense.getSubmittedBy())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submitted by (employeeId) is required");
            }
            if (!expense.getSubmittedBy().equals(currentExpense.getSubmittedBy())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change the submittedBy field");
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
            
            // Update only the fields that are present in the request (except submittedBy)
            if (expense.getMainHead() != null) updatedExpense.setMainHead(expense.getMainHead());
            if (expense.getExpenseHead() != null) updatedExpense.setExpenseHead(expense.getExpenseHead());
            if (expense.getVendor() != null) updatedExpense.setVendor(expense.getVendor());
            if (expense.getInitiated() != null) updatedExpense.setInitiated(expense.getInitiated());
            if (expense.getStatus() != null) updatedExpense.setStatus(expense.getStatus());
            if (expense.getCategory() != "") updatedExpense.setCategory(expense.getCategory());
            if (expense.getGstCredit() != "") updatedExpense.setGstCredit(expense.getGstCredit());
            if (expense.getFile() != "") updatedExpense.setFile(expense.getFile());
            if (expense.getTotalAmount() != null) updatedExpense.setTotalAmount(expense.getTotalAmount());
            if (expense.getAmountRequested() != null) updatedExpense.setAmountRequested(expense.getAmountRequested());
            if (expense.getComments() != null) updatedExpense.setComments(expense.getComments());
            
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
}