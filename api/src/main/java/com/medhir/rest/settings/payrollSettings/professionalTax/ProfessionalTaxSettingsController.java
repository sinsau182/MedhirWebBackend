package com.medhir.rest.settings.payrollSettings.professionalTax;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/professional-tax-settings")
public class ProfessionalTaxSettingsController {

    @Autowired
    private ProfessionalTaxSettingsService professionalTaxSettingsService;

    @GetMapping
    public ResponseEntity<ProfessionalTaxSettings> getProfessionalTaxSettings() {
        return ResponseEntity.ok(professionalTaxSettingsService.getProfessionalTaxSettings());
    }

    @PostMapping
    public ResponseEntity<ProfessionalTaxSettings> createProfessionalTaxSettings(
            @Valid @RequestBody ProfessionalTaxSettings settings) {
        return ResponseEntity.ok(professionalTaxSettingsService.createProfessionalTaxSettings(settings));
    }

    @PutMapping
    public ResponseEntity<ProfessionalTaxSettings> updateProfessionalTaxSettings(
            @Valid @RequestBody ProfessionalTaxSettings settings) {
        return ResponseEntity.ok(professionalTaxSettingsService.updateProfessionalTaxSettings(settings));
    }
}