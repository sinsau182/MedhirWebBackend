package com.medhir.rest.settings.department;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends MongoRepository<DepartmentModel, String> {
    Optional<DepartmentModel> findByName(String name);
    boolean existsByName(String name);
    Optional<DepartmentModel> findByDepartmentId(String departmentId);
    boolean existsByDepartmentId(String departmentId);
    List<DepartmentModel> findByCompanyId(String companyId);
} 