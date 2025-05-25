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
                    String employeeId = values[1].trim();
                    if (employeeId.isEmpty()) {
                        skippedRows.add("Line " + lineNumber + ": Empty Employee ID");
                        continue;
                    }

                    // Check if record exists for this employee, month and year
                    Optional<AttendanceRecord> existingRecord = attendanceRecordRepository
                            .findByEmployeeIdAndMonthAndYear(employeeId, month, year);
                    AttendanceRecord record;

                    if (existingRecord.isPresent()) {
                        // Use existing record as base
                        record = existingRecord.get();
                    } else {
                        // Create new record
                        record = new AttendanceRecord();
                        record.setMonth(month);
                        record.setYear(year);
                        record.setEmployeeId(employeeId);
                    }

                    // Update employee name if provided
                    if (!values[2].trim().isEmpty()) {
                        record.setEmployeeName(values[2].trim());
                    }

                    // Process weekly holidays if provided
                    String weeklyHolidayStr = values[3].trim();
                    if (!weeklyHolidayStr.isEmpty()) {
                        weeklyHolidayStr = weeklyHolidayStr.replace("\"", "");
                        List<String> weeklyHolidays = Arrays.stream(weeklyHolidayStr.split(","))
                                .map(String::trim)
                                .collect(Collectors.toList());
                        record.setWeeklyHoliday(weeklyHolidays);
                    }

                    // Update working days if provided
                    try {
                        String workingDaysStr = values[4].trim();
                        workingDaysStr = workingDaysStr.replace("\"", "");
                        if (!workingDaysStr.isEmpty()) {
                            record.setWorkingDays(Integer.parseInt(workingDaysStr));
                        }
                    } catch (NumberFormatException e) {
                        skippedRows.add("Line " + lineNumber + ": Invalid working days value: [" + values[4] + "]");
                        continue;
                    }

                    // Process daily attendance
                    Map<String, String> dailyAttendance = record.getDailyAttendance() != null
                            ? new LinkedHashMap<>(record.getDailyAttendance())
                            : new LinkedHashMap<>();

                    for (int i = 5; i < Math.min(values.length, 36); i++) {
                        String value = values[i].trim();
                        if (!value.isEmpty()) {
                            dailyAttendance.put(String.valueOf(i - 4), value);
                        }
                    }
                    record.setDailyAttendance(dailyAttendance);

                    // Update summary data if provided
                    if (values.length > 36) {
                        if (!values[36].trim().isEmpty())
                            record.setPayableDays(parseDouble(values[36], "Payable Days", lineNumber));
                        if (!values[37].trim().isEmpty())
                            record.setLeavesTaken(parseDouble(values[37], "Leaves Taken", lineNumber));
                        if (!values[38].trim().isEmpty())
                            record.setLeavesEarned(parseDouble(values[38], "Leaves Earned", lineNumber));
                        if (!values[39].trim().isEmpty())
                            record.setCompOffEarned(parseDouble(values[39], "Comp Off Earned", lineNumber));
                        if (!values[40].trim().isEmpty())
                            record.setLastMonthBalance(parseDouble(values[40], "Last Month Balance", lineNumber));
                        if (!values[41].trim().isEmpty())
                            record.setNetLeaveBalance(parseDouble(values[41], "Net Leave Balance", lineNumber));
                        if (!values[42].trim().isEmpty())
                            record.setPayableLeaves(parseDouble(values[42], "Payable Leaves", lineNumber));
                        if (!values[43].trim().isEmpty())
                            record.setLeavesPaid(parseDouble(values[43], "Leaves Paid", lineNumber));
                    }

                    records.add(record);
                } catch (Exception e) {
                    skippedRows.add("Line " + lineNumber + ": " + e.getMessage());
                }
            }

            if (records.isEmpty()) {
                throw new RuntimeException(
                        "No valid records found in the file. Skipped rows:\n" + String.join("\n", skippedRows));
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