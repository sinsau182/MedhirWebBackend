package com.medhir.rest.settings.leaveSettings.leavepolicy;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeavePolicyRepository extends MongoRepository<LeavePolicyModel, String> {
    Optional<LeavePolicyModel> findByName(String name);
    boolean existsByName(String name);
    Optional<LeavePolicyModel> findByLeavePolicyId(String leavePolicyId);
    boolean existsByLeavePolicyId(String leavePolicyId);
    List<LeavePolicyModel> findByCompanyId(String companyId);
} 