package com.medhir.rest.expenses;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;
import java.util.List;

public interface ExpenseRepository extends MongoRepository<Expense, String> {
    Optional<Expense> findByExpenseId(String expenseId);
    List<Expense> findBySubmittedBy(String employeeId);
} 