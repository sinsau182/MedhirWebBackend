package com.medhir.rest.income;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class IncomeService {
    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private GeneratedId generatedId;

    public Income createIncome(Income income) {
        try {
            income.setGeneratedId(generatedId);
            income.generateIncomeId();
            return incomeRepository.save(income);
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

    public Optional<Income> getIncomeById(String incomeId) {
        return incomeRepository.findByIncomeId(incomeId);
    }

    public Income updateIncome(String incomeId, Income income) {
        Optional<Income> existingIncome = incomeRepository.findByIncomeId(incomeId);
        if (existingIncome.isPresent()) {
            Income currentIncome = existingIncome.get();

            // Verify submittedBy is present but prevent it from being changed
            if (!StringUtils.hasText(income.getSubmittedBy())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submitted by (employeeId) is required");
            }
            if (!income.getSubmittedBy().equals(currentIncome.getSubmittedBy())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot change the submittedBy field");
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
            
            // Update only the fields that are present in the request
            if (income.getProject() != null) updatedIncome.setProject(income.getProject());
            if (income.getClient() != null) updatedIncome.setClient(income.getClient());
            if (income.getAmount() != null) updatedIncome.setAmount(income.getAmount());
            if (income.getInitiated() != null) updatedIncome.setInitiated(income.getInitiated());
            if (income.getStatus() != null) updatedIncome.setStatus(income.getStatus());
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
} 