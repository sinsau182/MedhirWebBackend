package com.medhir.rest.leave.repositoris;

import com.medhir.rest.leave.model.Leave;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends MongoRepository<Leave, String> {
    List<Leave> findByEmployeeId(String employeeId);
    List<Leave> findByStatus(String status);
    List<Leave> findByEmployeeIdAndStartDateAndLeaveType(String employeeId, LocalDate startDate, String leaveType);
    List<Leave> findByEmployeeIdAndStartDateAndStatus(String employeeId, LocalDate startDate, String status);
    Optional<Leave> findFirstByEmployeeIdAndStartDateAndStatusOrderByIdDesc(String employeeId, LocalDate startDate, String status);
    Optional<Leave> findByLeaveId(String leaveId);
}
