package com.medhir.rest.settings.payrollSettings.tdsSetting;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tds-settings")
public class TdsSettingsController {

    @Autowired
    private TdsSettingsService tdsSettingsService;

    @GetMapping
    public ResponseEntity<TdsSettings> getTdsSettings() {
        return ResponseEntity.ok(tdsSettingsService.getTdsSettings());
    }

    @PostMapping
    public ResponseEntity<TdsSettings> createTdsSettings(@Valid @RequestBody TdsSettings tdsSettings) {
        return ResponseEntity.ok(tdsSettingsService.createTdsSettings(tdsSettings));
    }

    @PutMapping
    public ResponseEntity<TdsSettings> updateTdsSettings(@Valid @RequestBody TdsSettings tdsSettings) {
        return ResponseEntity.ok(tdsSettingsService.updateTdsSettings(tdsSettings));
    }
}