package com.medhir.rest.income;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

public interface IncomeRepository extends MongoRepository<Income, String> {
    List<Income> findBySubmittedBy(String submittedBy);
    Optional<Income> findByIncomeId(String incomeId);
    List<Income> findBySubmittedByIn(List<String> submittedByList);
    List<Income> findByCompanyIdOrderBySubmittedBy(String companyId);
    List<Income> findByCompanyIdAndStatusOrderBySubmittedBy(String companyId, String status);
    List<Income> findBySubmittedByInAndStatusOrderBySubmittedBy(List<String> submittedByList, String status);
}