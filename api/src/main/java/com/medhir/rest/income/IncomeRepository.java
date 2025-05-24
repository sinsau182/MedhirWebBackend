package com.medhir.rest.income;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface IncomeRepository extends MongoRepository<Income, String> {
    Optional<Income> findByIncomeId(String incomeId);
    List<Income> findBySubmittedBy(String employeeId);
} 