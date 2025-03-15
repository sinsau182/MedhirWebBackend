package com.medhir.rest.controller;

import com.medhir.rest.model.CompanyModel;
import com.medhir.rest.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/superadmin/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCompany(@Valid @RequestBody CompanyModel company) {
        CompanyModel savedCompany = companyService.createCompany(company);
        return ResponseEntity.ok(Map.of(
                "message", "Company created successfully!",
                "company", savedCompany
        ));
    }

    @GetMapping
    public List<CompanyModel> getAllCompanies() {
        return companyService.getAllCompanies();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCompany(@PathVariable String id, @Valid @RequestBody CompanyModel company) {
        CompanyModel updatedCompany =  companyService.updateCompany(id, company);
        return ResponseEntity.ok(Map.of(
                "message", "Company updated successfully!",
                "Company ",updatedCompany
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCompany(@PathVariable String id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok(Map.of("message", "Company deleted successfully!"));
    }
}
