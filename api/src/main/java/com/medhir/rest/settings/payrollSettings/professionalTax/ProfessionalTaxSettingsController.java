package com.medhir.rest.settings.payrollSettings.professionalTax;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/professional-tax-settings")
public class ProfessionalTaxSettingsController {

    @Autowired
    private ProfessionalTaxSettingsService professionalTaxSettingsService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<ProfessionalTaxSettings> getProfessionalTaxSettingsByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(professionalTaxSettingsService.getProfessionalTaxSettingsByCompany(companyId));
    }

    @PostMapping
    public ResponseEntity<ProfessionalTaxSettings> createProfessionalTaxSettings(
            @Valid @RequestBody ProfessionalTaxSettings settings) {
        return ResponseEntity.ok(professionalTaxSettingsService.createProfessionalTaxSettings(settings));
    }

    @PutMapping("/company/{companyId}")
    public ResponseEntity<ProfessionalTaxSettings> updateProfessionalTaxSettings(
            @PathVariable String companyId,
            @Valid @RequestBody ProfessionalTaxSettings settings) {
        return ResponseEntity.ok(professionalTaxSettingsService.updateProfessionalTaxSettings(companyId, settings));
    }
}