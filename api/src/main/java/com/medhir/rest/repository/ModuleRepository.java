package com.medhir.rest.repository;

import com.medhir.rest.model.ModuleModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModuleRepository extends MongoRepository<ModuleModel, String> {}
