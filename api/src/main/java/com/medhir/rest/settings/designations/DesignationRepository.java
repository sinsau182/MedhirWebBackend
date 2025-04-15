package com.medhir.rest.settings.designations;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DesignationRepository extends MongoRepository<DesignationModel, String> {
    Optional<DesignationModel> findByName(String name);
    boolean existsByName(String name);
    Optional<DesignationModel> findByDesignationId(String designationId);
    boolean existsByDesignationId(String designationId);
    List<DesignationModel> findByDepartment(String department);
}