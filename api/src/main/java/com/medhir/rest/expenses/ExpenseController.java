package com.medhir.rest.expenses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.medhir.rest.expenses.dto.UpdateExpenseStatusRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Map;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping(value = "/employee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> createExpense(@Valid @RequestBody Expense expense, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + String.join(", ", errors));
        }
        try {
            return ResponseEntity.ok(expenseService.createExpense(expense));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Expense>> getAllExpenses() {
        return ResponseEntity.ok(expenseService.getAllExpenses());
    }

    @GetMapping(value = "/employee/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Expense>> getExpensesByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(expenseService.getExpensesByEmployee(employeeId));
    }

    

    @GetMapping(value = "/company/{companyId}/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Expense>> getExpensesByCompanyAndStatus(
            @PathVariable String companyId,
            @PathVariable String status) {
        return ResponseEntity.ok(expenseService.getExpensesByCompanyAndStatus(companyId, status));
    }

    @GetMapping(value = "/manager/{managerId}/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Expense>> getExpensesByManagerAndStatus(
            @PathVariable String managerId,
            @PathVariable String status) {
        return ResponseEntity.ok(expenseService.getExpensesByManagerAndStatus(managerId, status));
    }

    @GetMapping(value = "/{expenseId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> getExpenseById(@PathVariable String expenseId) {
        Optional<Expense> expense = expenseService.getExpenseById(expenseId);
        return expense.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/employee/{expenseId}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> updateExpense(
            @PathVariable String expenseId, 
            @RequestBody Expense expense) {
        try {
            return ResponseEntity.ok(expenseService.updateExpense(expenseId, expense));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/updateStatus/{expenseId}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> updateExpenseStatus(
            @PathVariable String expenseId,
            @Valid @RequestBody UpdateExpenseStatusRequest request,
            Authentication authentication) {
        try {
            // Verify HRADMIN role
            boolean isHrAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> "HRADMIN".equals(role));

            if (!isHrAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HRADMIN can access this endpoint");
            }

            Expense updatedExpense = expenseService.updateExpenseStatusByHrAdmin(
                expenseId, 
                request.getStatus(),
                request.getRemarks()
            );
            return ResponseEntity.ok(updatedExpense);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/manager/updateStatus/{expenseId}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> updateExpenseStatusByManager(
            @PathVariable String expenseId,
            @Valid @RequestBody UpdateExpenseStatusRequest request,
            Authentication authentication) {
        try {
            // Verify MANAGER role
            boolean isManager = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> "MANAGER".equals(role));

            if (!isManager) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only MANAGER can access this endpoint");
            }

            // Get current user's ID from security context details
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            String currentUserId = (String) details.get("employeeId");

            Expense updatedExpense = expenseService.updateExpenseStatusByManager(
                expenseId, 
                request.getStatus(),
                request.getRemarks(),
                currentUserId
            );
            return ResponseEntity.ok(updatedExpense);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
} 