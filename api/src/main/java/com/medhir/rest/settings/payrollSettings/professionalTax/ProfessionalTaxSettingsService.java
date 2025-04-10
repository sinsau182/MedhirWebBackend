package com.medhir.rest.settings.payrollSettings.professionalTax;

import com.medhir.rest.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProfessionalTaxSettingsService {

    @Autowired
    private ProfessionalTaxSettingsRepository repository;

    public ProfessionalTaxSettings getProfessionalTaxSettings() {
        return repository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Professional tax settings not found"));
    }

    public ProfessionalTaxSettings createProfessionalTaxSettings(ProfessionalTaxSettings settings) {
        // Check if settings already exist
        Optional<ProfessionalTaxSettings> existingSettings = repository.findFirstByOrderByCreatedAtDesc();
        if (existingSettings.isPresent()) {
            throw new ResourceNotFoundException("Professional tax settings already exist. Use update endpoint to modify.");
        }

        // Validate settings
        validateProfessionalTaxSettings(settings);

        // Set timestamps
        settings.setCreatedAt(LocalDateTime.now().toString());
        settings.setUpdatedAt(LocalDateTime.now().toString());

        return repository.save(settings);
    }

    public ProfessionalTaxSettings updateProfessionalTaxSettings(ProfessionalTaxSettings settings) {
        // Get existing settings
        ProfessionalTaxSettings existingSettings = repository.findFirstByOrderByCreatedAtDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Professional tax settings not found"));

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