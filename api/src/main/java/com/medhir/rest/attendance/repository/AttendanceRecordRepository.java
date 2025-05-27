package com.medhir.rest.attendance.repository;

import com.medhir.rest.attendance.model.AttendanceRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRecordRepository extends MongoRepository<AttendanceRecord, String> {
    List<AttendanceRecord> findByMonthAndYear(String month, String year);
    Optional<AttendanceRecord> findByEmployeeIdAndMonthAndYear(String employeeId, String month, String year);
    List<AttendanceRecord> findByEmployeeId(String employeeId);
    
    @Query("{ 'month': ?0, 'year': ?1, 'dailyAttendance.?2': { $exists: true } }")
    List<AttendanceRecord> findByMonthYearAndDate(String month, String year, String date);
    
    @Query("{ 'month': ?0, 'year': ?1, 'dailyAttendance.?2': ?3 }")
    List<AttendanceRecord> findByMonthYearDateAndStatus(String month, String year, String date, String status);
} 