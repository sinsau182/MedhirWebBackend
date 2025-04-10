package com.medhir.rest.settings.payrollSettings.tdsSetting;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TdsSettingsRepository extends MongoRepository<TdsSettings, String> {
    Optional<TdsSettings> findFirstByOrderByCreatedAtDesc();
}