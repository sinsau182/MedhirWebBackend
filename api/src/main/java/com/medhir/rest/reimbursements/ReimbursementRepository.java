package com.medhir.rest.reimbursements;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.medhir.rest.reimbursements.ReimbursementModel;

public interface ReimbursementRepository extends MongoRepository<ReimbursementModel, String> {
    // Custom queries if needed
}
