package com.medhir.rest.attendance.controller;

import com.medhir.rest.attendance.model.AttendanceRecord;
import com.medhir.rest.attendance.service.AttendanceRecordService;
import com.medhir.rest.attendance.dto.DailyAttendanceDTO;
import com.medhir.rest.attendance.dto.FilteredAttendanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceRecordController {
    private final AttendanceRecordService attendanceRecordService;

    /**
     * Upload attendance records from an Excel/CSV file for a specific month and year
     * @param file - The attendance file to upload
     * @param month - Month for which attendance is being uploaded
     * @param year - Year for which attendance is being uploaded
     * @return Success message with count of records uploaded or error message
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadAttendanceRecords(
            @RequestParam("file") MultipartFile file,
            @RequestParam("month") String month,
            @RequestParam("year") String year) {
        try {
            List<AttendanceRecord> records = attendanceRecordService.processAndSaveAttendanceRecords(file, month, year);
            return ResponseEntity.ok(Map.of(
                "message", "Attendance records uploaded successfully",
                "count", records.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all attendance records for a specific month and year
     * @param month - Month to fetch records for
     * @param year - Year to fetch records for
     * @return List of attendance records or error message
     */
    @GetMapping("/month/{month}/year/{year}")
    public ResponseEntity<?> getAttendanceRecordsByMonthAndYear(
            @PathVariable String month,
            @PathVariable String year) {
        try {
            List<AttendanceRecord> records = attendanceRecordService.getAttendanceRecordsByMonthAndYear(month, year);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get attendance record for a specific employee for a given month and year
     * @param employeeId - ID of the employee
     * @param month - Month to fetch record for (e.g., "Apr")
     * @param year - Year to fetch record for
     * @return Array of daily attendance records
     */
    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    // @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER', 'HR') and (authentication.principal.username == #employeeId or hasRole('MANAGER') or hasRole('HR'))")
    public ResponseEntity<?> getAttendanceRecordByEmployeeAndMonth(
            @PathVariable String employeeId,
            @PathVariable String month,
            @PathVariable String year,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Extract just the month name if it contains additional text
            String cleanMonth = month.split("-")[0];
            AttendanceRecord record = attendanceRecordService.getAttendanceRecordByEmployeeAndMonth(employeeId, cleanMonth, year);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all attendance records for a specific employee
     * @param employeeId - ID of the employee
     * @return List of attendance records or error message
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getAttendanceRecordsByEmployee(@PathVariable String employeeId) {
        try {
            List<AttendanceRecord> records = attendanceRecordService.getAttendanceRecordsByEmployee(employeeId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get filtered attendance records by status for a specific date, month, and year
     * @param month - Month to filter records
     * @param year - Year to filter records
     * @param date - Specific date to filter records
     * @param status - Attendance status to filter by (e.g., present, absent, late)
     * @return List of filtered attendance records or error message
     */
    @GetMapping("/month/{month}/year/{year}/date/{date}/status/{status}")
    public ResponseEntity<?> getAttendanceByStatus(
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable String date,
            @PathVariable String status) {
        try {
            List<FilteredAttendanceDTO> records = attendanceRecordService.getAttendanceByStatus(month, year, date, status);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Get all employees' attendance for a specific date
     * @param month - Month to fetch records for
     * @param year - Year to fetch records for
     * @param date - Specific date to fetch records for
     * @return List of daily attendance records
     */
    @GetMapping("/month/{month}/year/{year}/date/{date}")
    public ResponseEntity<List<DailyAttendanceDTO>> getDailyAttendanceByDate(
            @PathVariable String month,
            @PathVariable String year,
            @PathVariable String date) {
        try {
            List<DailyAttendanceDTO> records = attendanceRecordService.getDailyAttendanceByDate(month, year, date);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 