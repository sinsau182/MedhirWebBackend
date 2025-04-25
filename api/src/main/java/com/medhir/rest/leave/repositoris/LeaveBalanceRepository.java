package com.medhir.rest.leave.repositoris;

import com.medhir.rest.leave.model.LeaveBalance;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends MongoRepository<LeaveBalance, String> {
    Optional<LeaveBalance> findByEmployeeIdAndMonthAndYear(String employeeId, String month, int year);
    List<LeaveBalance> findByEmployeeIdAndYear(String employeeId, int year);
    
    // Find the most recent balance for an employee
    @Query("{'employeeId': ?0}")
    Optional<LeaveBalance> findTopByEmployeeIdOrderByYearDescNumericMonthDesc(String employeeId, Sort sort);
    
    // Find previous month's balance
    Optional<LeaveBalance> findByEmployeeIdAndNumericMonthAndYear(String employeeId, int previousMonth, int previousYear);
} 