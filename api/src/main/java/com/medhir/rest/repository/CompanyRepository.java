package com.medhir.rest.repository;

import com.medhir.rest.model.CompanyModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CompanyRepository extends MongoRepository<CompanyModel, String> {
    Optional<CompanyModel> findByEmail(String email);

    Optional<CompanyModel> findByPhone(String phone);

    Optional<CompanyModel> findByCompanyId(String companyId);

    boolean existsByCompanyId(String companyId);

    void deleteByCompanyId(String companyId);
}