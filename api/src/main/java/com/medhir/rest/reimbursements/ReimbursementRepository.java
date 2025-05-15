package com.medhir.rest.reimbursements;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.medhir.rest.reimbursements.ReimbursementModel;
import java.util.List;

public interface ReimbursementRepository extends MongoRepository<ReimbursementModel, String> {
    List<ReimbursementModel> findByEmployeeId(String employeeId);
}
