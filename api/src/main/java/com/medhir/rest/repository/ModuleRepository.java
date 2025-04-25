package com.medhir.rest.repository;

import com.medhir.rest.model.ModuleModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ModuleRepository extends MongoRepository<ModuleModel, String> {
    Optional<ModuleModel> findByModuleId(String moduleId);
}
