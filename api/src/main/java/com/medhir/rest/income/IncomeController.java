package com.medhir.rest.income;

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
import com.medhir.rest.income.dto.UpdateIncomeStatusRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Map;

@RestController
@RequestMapping("/income")
public class IncomeController {
    @Autowired
    private IncomeService incomeService;

    @PostMapping(value = "/employee", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Income> createIncome(@Valid @RequestBody Income income, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + String.join(", ", errors));
        }
        try {
            return ResponseEntity.ok(incomeService.createIncome(income));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Income>> getAllIncomes() {
        return ResponseEntity.ok(incomeService.getAllIncomes());
    }

    @GetMapping(value = "/employee/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Income>> getIncomesByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(incomeService.getIncomesByEmployee(employeeId));
    }


    @GetMapping(value = "/company/{companyId}/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Income>> getIncomesByCompanyAndStatus(
            @PathVariable String companyId,
            @PathVariable String status) {
        return ResponseEntity.ok(incomeService.getIncomesByCompanyAndStatus(companyId, status));
    }

    @GetMapping(value = "/manager/{managerId}/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Income>> getIncomesByManagerAndStatus(
            @PathVariable String managerId,
            @PathVariable String status) {
        return ResponseEntity.ok(incomeService.getIncomesByManagerAndStatus(managerId, status));
    }


    @GetMapping(value = "/{incomeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Income> getIncomeById(@PathVariable String incomeId) {
        Optional<Income> income = incomeService.getIncomeById(incomeId);
        return income.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/employee/{incomeId}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Income> updateIncome(
            @PathVariable String incomeId, 
            @RequestBody Income income) {
        try {
            return ResponseEntity.ok(incomeService.updateIncome(incomeId, income));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PutMapping(value = "/updateStatus/{incomeId}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Income> updateIncomeStatus(
            @PathVariable String incomeId,
            @Valid @RequestBody UpdateIncomeStatusRequest request,
            Authentication authentication) {
        try {
            // Verify HRADMIN role
            boolean isHrAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> "HRADMIN".equals(role));

            if (!isHrAdmin) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only HRADMIN can access this endpoint");
            }

            Income updatedIncome = incomeService.updateIncomeStatusByHrAdmin(
                incomeId, 
                request.getStatus(),
                request.getRemarks()
            );
            return ResponseEntity.ok(updatedIncome);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @DeleteMapping(value = "/employee/{incomeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> deleteIncome(@PathVariable String incomeId) {
        incomeService.deleteIncome(incomeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/manager/updateStatus/{incomeId}", 
                consumes = MediaType.APPLICATION_JSON_VALUE, 
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Income> updateIncomeStatusByManager(
            @PathVariable String incomeId,
            @Valid @RequestBody UpdateIncomeStatusRequest request,
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

            Income updatedIncome = incomeService.updateIncomeStatusByManager(
                incomeId, 
                request.getStatus(),
                request.getRemarks(),
                currentUserId
            );
            return ResponseEntity.ok(updatedIncome);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
} 