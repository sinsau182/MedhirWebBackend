package com.medhir.rest.repository;

import com.medhir.rest.model.EmployeeModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends MongoRepository<EmployeeModel, String> {
    Optional<EmployeeModel> findByEmail(String email);
    Optional<EmployeeModel> findByPhone(String phone);
}
