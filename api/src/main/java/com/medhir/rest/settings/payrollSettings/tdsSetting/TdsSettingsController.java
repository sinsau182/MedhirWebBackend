package com.medhir.rest.settings.payrollSettings.tdsSetting;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tds-settings")
public class TdsSettingsController {

    @Autowired
    private TdsSettingsService tdsSettingsService;

    @GetMapping("/company/{companyId}")
    public ResponseEntity<TdsSettings> getTdsSettingsByCompany(@PathVariable String companyId) {
        return ResponseEntity.ok(tdsSettingsService.getTdsSettingsByCompany(companyId));
    }

    @PostMapping
    public ResponseEntity<TdsSettings> createTdsSettings(@Valid @RequestBody TdsSettings tdsSettings) {
        return ResponseEntity.ok(tdsSettingsService.createTdsSettings(tdsSettings));
    }

    @PutMapping("/company/{companyId}")
    public ResponseEntity<TdsSettings> updateTdsSettings(
            @PathVariable String companyId,
            @Valid @RequestBody TdsSettings tdsSettings) {
        return ResponseEntity.ok(tdsSettingsService.updateTdsSettings(companyId, tdsSettings));
    }
}