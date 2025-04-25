package com.medhir.rest.settings.payrollSettings.professionalTax;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessionalTaxSettingsRepository extends MongoRepository<ProfessionalTaxSettings, String> {
    Optional<ProfessionalTaxSettings> findFirstByCompanyIdOrderByCreatedAtDesc(String companyId);
}