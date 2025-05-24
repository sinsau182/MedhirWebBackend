package com.medhir.rest.attendance.controller;

import com.medhir.rest.attendance.model.AttendanceRecord;
import com.medhir.rest.attendance.service.AttendanceRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceRecordController {
    private final AttendanceRecordService attendanceRecordService;

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

    @GetMapping("/employee/{employeeId}/month/{month}/year/{year}")
    public ResponseEntity<?> getAttendanceRecordByEmployeeAndMonth(
            @PathVariable String employeeId,
            @PathVariable String month,
            @PathVariable String year) {
        try {
            AttendanceRecord record = attendanceRecordService.getAttendanceRecordByEmployeeAndMonth(employeeId, month, year);
            return ResponseEntity.ok(record);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getAttendanceRecordsByEmployee(@PathVariable String employeeId) {
        try {
            List<AttendanceRecord> records = attendanceRecordService.getAttendanceRecordsByEmployee(employeeId);
            return ResponseEntity.ok(records);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 