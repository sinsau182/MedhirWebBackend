package com.medhir.rest.settings.leaveSettings.publicHolidays;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/public-holidays")
@CrossOrigin(origins = "*")
public class PublicHolidaysController {

    private final PublicHolidayService publicHolidayService;

    @Autowired
    public PublicHolidaysController(PublicHolidayService publicHolidayService) {
        this.publicHolidayService = publicHolidayService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPublicHoliday(@Valid @RequestBody PublicHolidayModel holiday) {
        holiday.setHolidayId(null); // Clear any existing holidayId
        PublicHolidayModel createdHoliday = publicHolidayService.createPublicHoliday(holiday);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public holiday created successfully");
        response.put("holidayId", createdHoliday.getHolidayId());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PublicHolidayModel>> getAllPublicHolidays() {
        return ResponseEntity.ok(publicHolidayService.getAllPublicHolidays());
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<PublicHolidayModel>> getPublicHolidaysByCompanyId(@PathVariable String companyId) {
        return ResponseEntity.ok(publicHolidayService.getPublicHolidaysByCompanyId(companyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PublicHolidayModel> getPublicHolidayById(@PathVariable String id) {
        return ResponseEntity.ok(publicHolidayService.getPublicHolidayById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updatePublicHoliday(
            @PathVariable String id,
            @Valid @RequestBody PublicHolidayModel holiday) {
        holiday.setHolidayId(null); // Clear any existing holidayId
        publicHolidayService.updatePublicHoliday(id, holiday);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public holiday updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePublicHoliday(@PathVariable String id) {
        publicHolidayService.deletePublicHoliday(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Public holiday deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exists/{holidayName}")
    public ResponseEntity<Boolean> checkHolidayExists(@PathVariable String holidayName) {
        boolean exists = publicHolidayService.existsByHolidayName(holidayName);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}