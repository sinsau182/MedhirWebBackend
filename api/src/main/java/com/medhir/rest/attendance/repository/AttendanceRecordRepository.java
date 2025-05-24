package com.medhir.rest.attendance.repository;

import com.medhir.rest.attendance.model.AttendanceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends MongoRepository<AttendanceRecord, String> {
    List<AttendanceRecord> findByMonthAndYear(String month, String year);
    Optional<AttendanceRecord> findByEmployeeIdAndMonthAndYear(String employeeId, String month, String year);
    List<AttendanceRecord> findByEmployeeId(String employeeId);
} 