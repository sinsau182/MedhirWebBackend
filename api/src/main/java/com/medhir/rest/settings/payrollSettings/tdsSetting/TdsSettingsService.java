package com.medhir.rest.settings.payrollSettings.tdsSetting;

import com.medhir.rest.exception.DuplicateResourceException;
import com.medhir.rest.exception.ResourceNotFoundException;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TdsSettingsService {

    @Autowired
    private TdsSettingsRepository tdsSettingsRepository;

    @Autowired
    private GeneratedId generatedId;

    public List<TdsSettings> getAllTdsSettings() {
        return tdsSettingsRepository.findAll();
    }


    public TdsSettings getTdsSettingsByCompany(String companyId) {
        return tdsSettingsRepository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("TDS settings not found for company: " + companyId));
    }

    public TdsSettings createTdsSettings(TdsSettings tdsSettings) {
        // Check if settings already exist for the company
        Optional<TdsSettings> existingSettings = tdsSettingsRepository.findFirstByCompanyIdOrderByCreatedAtDesc(tdsSettings.getCompanyId());
        if (existingSettings.isPresent()) {
            throw new DuplicateResourceException("TDS settings already exist for this company. Use update endpoint to modify.");
        }

        // Validate settings
        validateTdsSettings(tdsSettings);

        // Set timestamps
        tdsSettings.setCreatedAt(LocalDateTime.now().toString());
        tdsSettings.setUpdatedAt(LocalDateTime.now().toString());

        return tdsSettingsRepository.save(tdsSettings);
    }

    public TdsSettings updateTdsSettings(String companyId, TdsSettings tdsSettings) {
        // Get existing settings for the company
        TdsSettings existingSettings = tdsSettingsRepository.findFirstByCompanyIdOrderByCreatedAtDesc(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("TDS settings not found for company: " + companyId));

        // Set the company ID in the settings object
        tdsSettings.setCompanyId(companyId);

        // Validate settings
        validateTdsSettings(tdsSettings);

        // Update fields
        existingSettings.setTdsRate(tdsSettings.getTdsRate());
        existingSettings.setDescription(tdsSettings.getDescription());
        existingSettings.setUpdatedAt(LocalDateTime.now().toString());

        return tdsSettingsRepository.save(existingSettings);
    }

    public void deleteTdsSettings(String id) {
        TdsSettings settings = tdsSettingsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TDS Settings not found with id: " + id));
        tdsSettingsRepository.deleteById(settings.getId());
    }

    private void validateTdsSettings(TdsSettings settings) {
        if (settings.getTdsRate() == null || settings.getTdsRate() < 0 || settings.getTdsRate() > 100) {
            throw new ResourceNotFoundException("TDS rate must be between 0 and 100");
        }

        if (settings.getDescription() == null || settings.getDescription().trim().isEmpty()) {
            throw new ResourceNotFoundException("Description cannot be empty");
        }
    }
}