package com.medhir.rest.attendance.service;

import com.medhir.rest.attendance.model.AttendanceRecord;
import com.medhir.rest.attendance.repository.AttendanceRecordRepository;
import com.medhir.rest.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceRecordService {
    private final AttendanceRecordRepository attendanceRecordRepository;

    public List<AttendanceRecord> processAndSaveAttendanceRecords(MultipartFile file, String month, String year) {
        List<AttendanceRecord> records = new ArrayList<>();
        int lineNumber = 0;
        List<String> skippedRows = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            // Skip header rows
            reader.readLine(); // Skip Month row
            reader.readLine(); // Skip SL No row
            lineNumber = 2;
            
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // Split by comma but preserve quoted values
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                
                // Skip empty rows or rows with insufficient data
                if (values.length < 5 || isRowEmpty(values)) {
                    skippedRows.add("Line " + lineNumber + ": Insufficient data or empty row");
                    continue;
                }
                
                try {
                    AttendanceRecord record = new AttendanceRecord();
                    record.setMonth(month);
                    record.setYear(year);
                    
                    // Validate and set required fields
                    if (values[1].trim().isEmpty()) {
                        skippedRows.add("Line " + lineNumber + ": Empty Employee ID");
                        continue;
                    }
                    record.setEmployeeId(values[1].trim());
                    
                    if (values[2].trim().isEmpty()) {
                        skippedRows.add("Line " + lineNumber + ": Empty Employee Name");
                        continue;
                    }
                    record.setEmployeeName(values[2].trim());
                    
                    // Process weekly holidays
                    String weeklyHolidayStr = values[3].trim();
                    if (!weeklyHolidayStr.isEmpty()) {
                        // Remove any quotes and split by comma , then trim each value
                        weeklyHolidayStr = weeklyHolidayStr.replace("\"", "");
                        List<String> weeklyHolidays = Arrays.stream(weeklyHolidayStr.split(","))
                            .map(String::trim)
                            .collect(Collectors.toList());
                        record.setWeeklyHoliday(weeklyHolidays);
                    } else {
                        record.setWeeklyHoliday(new ArrayList<>());
                    }
                    
                    // Parse working days with validation
                    try {
                        String workingDaysStr = values[4].trim();
                        // Remove any quotes
                        workingDaysStr = workingDaysStr.replace("\"", "");
                        if (workingDaysStr.isEmpty()) {
                            skippedRows.add("Line " + lineNumber + ": Empty working days value");
                            continue;
                        }
                        record.setWorkingDays(Integer.parseInt(workingDaysStr));
                    } catch (NumberFormatException e) {
                        skippedRows.add("Line " + lineNumber + ": Invalid working days value: [" + values[4] + "]");
                        continue;
                    }
                    
                    // Process daily attendance
                    Map<String, String> dailyAttendance = new LinkedHashMap<>(); // Using LinkedHashMap to preserve insertion order
                    for (int i = 5; i < Math.min(values.length, 36); i++) {
                        String value = values[i].trim();
                        if (!value.isEmpty()) {
                            dailyAttendance.put(String.valueOf(i - 4), value);
                        }
                    }
                    record.setDailyAttendance(dailyAttendance);
                    
                    // Process summary data with validation
                    if (values.length > 36) {
                        record.setPayableDays(parseDouble(values[36], "Payable Days", lineNumber));
                        record.setLeavesTaken(parseDouble(values[37], "Leaves Taken", lineNumber));
                        record.setLeavesEarned(parseDouble(values[38], "Leaves Earned", lineNumber));
                        record.setCompOffEarned(parseDouble(values[39], "Comp Off Earned", lineNumber));
                        record.setLastMonthBalance(parseDouble(values[40], "Last Month Balance", lineNumber));
                        record.setNetLeaveBalance(parseDouble(values[41], "Net Leave Balance", lineNumber));
                        record.setPayableLeaves(parseDouble(values[42], "Payable Leaves", lineNumber));
                        record.setLeavesPaid(parseDouble(values[43], "Leaves Paid", lineNumber));
                    }
                    
                    records.add(record);
                } catch (Exception e) {
                    skippedRows.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }
            
            if (records.isEmpty()) {
                throw new RuntimeException("No valid records found in the file. Skipped rows:\n" + String.join("\n", skippedRows));
            }
            
            // Save all records
            List<AttendanceRecord> savedRecords = attendanceRecordRepository.saveAll(records);
            
            // If there were any skipped rows, include them in the response
            if (!skippedRows.isEmpty()) {
                System.out.println("Skipped rows:\n" + String.join("\n", skippedRows));
            }
            
            return savedRecords;
            
        } catch (Exception e) {
            throw new RuntimeException("Error processing attendance records: " + e.getMessage() + 
                (!skippedRows.isEmpty() ? "\nSkipped rows:\n" + String.join("\n", skippedRows) : ""));
        }
    }

    private boolean isRowEmpty(String[] values) {
        return values.length == 0 || Arrays.stream(values).allMatch(String::isEmpty);
    }

    private double parseDouble(String value, String fieldName, int lineNumber) {
        try {
            value = value.trim().replace("\"", "");
            return value.isEmpty() ? 0.0 : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid " + fieldName + " value: [" + value + "]");
        }
    }

    public List<AttendanceRecord> getAttendanceRecordsByMonthAndYear(String month, String year) {
        List<AttendanceRecord> records = attendanceRecordRepository.findByMonthAndYear(month, year);
        return records.stream()
            .map(this::sortDailyAttendance)
            .collect(Collectors.toList());
    }

    public AttendanceRecord getAttendanceRecordByEmployeeAndMonth(String employeeId, String month, String year) {
        AttendanceRecord record = attendanceRecordRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));
        return sortDailyAttendance(record);
    }

    public List<AttendanceRecord> getAttendanceRecordsByEmployee(String employeeId) {
        List<AttendanceRecord> records = attendanceRecordRepository.findByEmployeeId(employeeId);
        return records.stream()
            .map(this::sortDailyAttendance)
            .collect(Collectors.toList());
    }

    private AttendanceRecord sortDailyAttendance(AttendanceRecord record) {
        if (record.getDailyAttendance() != null) {
            // Convert the map to a sorted map
            Map<String, String> sortedAttendance = new TreeMap<>(Comparator.comparingInt(Integer::parseInt));
            sortedAttendance.putAll(record.getDailyAttendance());
            record.setDailyAttendance(sortedAttendance);
        }
        return record;
    }
} 