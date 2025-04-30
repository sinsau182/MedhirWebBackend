package com.medhir.rest.settings.designations;

import com.medhir.rest.dto.CompanyDesignationDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    @PostMapping
    public ResponseEntity<DesignationModel> createDesignation(@Valid @RequestBody DesignationModel designation) {
        designation.setDesignationId(null);
        return ResponseEntity.ok(designationService.createDesignation(designation));
    }

    @GetMapping
    public ResponseEntity<List<DesignationModel>> getAllDesignations() {
        return ResponseEntity.ok(designationService.getAllDesignations());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<DesignationModel>> getDesignationsByDepartment(@PathVariable String departmentId) {
        return ResponseEntity.ok(designationService.getDesignationsByDepartment(departmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DesignationModel> getDesignationById(@PathVariable String id) {
        return ResponseEntity.ok(designationService.getDesignationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DesignationModel> updateDesignation(
            @PathVariable String id,
            @Valid @RequestBody DesignationModel designation) {
        designation.setDesignationId(null);
        return ResponseEntity.ok(designationService.updateDesignation(id, designation));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDesignation(@PathVariable String id) {
        designationService.deleteDesignation(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<CompanyDesignationDTO>> getAllDesignationsByCompanyId(@PathVariable String companyId) {
        List<CompanyDesignationDTO> designations = designationService.getAllDesignationsByCompanyId(companyId);
        return ResponseEntity.ok(designations);
    }
}