package com.medhir.rest.expenses;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    List<Expense> findBySubmittedBy(String submittedBy);
    Optional<Expense> findByExpenseId(String expenseId);
    List<Expense> findBySubmittedByIn(List<String> submittedByList);
    List<Expense> findByCompanyIdOrderBySubmittedBy(String companyId);
    List<Expense> findByCompanyIdAndStatusOrderBySubmittedBy(String companyId, String status);
    List<Expense> findBySubmittedByInAndStatusOrderBySubmittedBy(List<String> submittedByList, String status);
}