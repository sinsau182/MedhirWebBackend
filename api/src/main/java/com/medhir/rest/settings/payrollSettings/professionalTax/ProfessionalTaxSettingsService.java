package com.medhir.rest.settings.payrollSettings.professionalTax;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfessionalTaxSettingsService {

    @Autowired
    private ProfessionalTaxSettingsRepository repository;

    public ProfessionalTaxSettings getProfessionalTaxSettingsByCompany(String companyId) {
        return repository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Professional tax settings not found for company: " + companyId));
    }

    public ProfessionalTaxSettings createProfessionalTaxSettings(ProfessionalTaxSettings settings) {
        // Check if settings already exist for the company
        Optional<ProfessionalTaxSettings> existingSettings = repository.findFirstByCompanyIdOrderByCreatedAtDesc(settings.getCompanyId());
        if (existingSettings.isPresent()) {
            throw new DuplicateResourceException("Professional tax settings already exist for this company. Use update endpoint to modify.");
        }

        // Validate settings
        validateProfessionalTaxSettings(settings);

        // Set timestamps
        settings.setCreatedAt(LocalDateTime.now().toString());
        settings.setUpdatedAt(LocalDateTime.now().toString());

        return repository.save(settings);
    }

    public ProfessionalTaxSettings updateProfessionalTaxSettings(String companyId, ProfessionalTaxSettings settings) {
        // Get existing settings for the company
        ProfessionalTaxSettings existingSettings = repository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Professional tax settings not found for company: " + companyId));

        // Set the company ID in the settings object
        settings.setCompanyId(companyId);

        // Validate settings
        validateProfessionalTaxSettings(settings);

        // Update fields
        existingSettings.setMonthlySalaryThreshold(settings.getMonthlySalaryThreshold());
        existingSettings.setAmountAboveThreshold(settings.getAmountAboveThreshold());
        existingSettings.setAmountBelowThreshold(settings.getAmountBelowThreshold());
        existingSettings.setDescription(settings.getDescription());
        existingSettings.setUpdatedAt(LocalDateTime.now().toString());

        return repository.save(existingSettings);
    }

    private void validateProfessionalTaxSettings(ProfessionalTaxSettings settings) {
        if (settings.getMonthlySalaryThreshold() == null || settings.getMonthlySalaryThreshold() <= 0) {
            throw new ResourceNotFoundException("Monthly salary threshold must be a positive number");
        }

        if (settings.getAmountAboveThreshold() == null || settings.getAmountAboveThreshold() < 0) {
            throw new ResourceNotFoundException("Amount above threshold cannot be negative");
        }

        if (settings.getAmountBelowThreshold() == null || settings.getAmountBelowThreshold() < 0) {
            throw new ResourceNotFoundException("Amount below threshold cannot be negative");
        }

        if (settings.getDescription() == null || settings.getDescription().trim().isEmpty()) {
            throw new ResourceNotFoundException("Description cannot be empty");
        }
    }
}