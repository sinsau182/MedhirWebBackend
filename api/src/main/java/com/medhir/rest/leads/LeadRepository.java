package com.medhir.rest.leads;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LeadRepository extends MongoRepository<Lead, String> {
    Optional<Lead> findByName(String name);
    boolean existsByContactNumber(String contactNumber);
    boolean existsByEmail(String email);
    Optional<Lead> findByLeadId(String leadId);
    // Custom query methods can be added here if needed
} 