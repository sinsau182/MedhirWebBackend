package com.medhir.rest.reimbursements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.medhir.rest.reimbursements.ReimbursementModel;
import com.medhir.rest.reimbursements.ReimbursementService;
import com.medhir.rest.service.MinioService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reimbursements")
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;

    @Autowired
    private MinioService minioService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReimbursementModel> createReimbursement(
        @RequestParam String employeeId,
        @RequestParam String employeeName,
        @RequestParam String department,
        @RequestParam String category,
        @RequestParam String reimbursementType,
        @RequestParam String description,
        @RequestParam Double amount,
        @RequestParam("receipt") MultipartFile receiptFile
    ) {
        // Upload the file to MinIO using the document bucket
        String receiptUrl = minioService.uploadDocumentsImg(receiptFile, employeeId);

        // Create and save the reimbursement
        ReimbursementModel reimbursement = new ReimbursementModel();
        reimbursement.setEmployeeId(employeeId);
        reimbursement.setEmployeeName(employeeName);
        reimbursement.setDepartment(department);
        reimbursement.setCategory(category);
        reimbursement.setReimbursementType(reimbursementType);
        reimbursement.setDescription(description);
        reimbursement.setAmount(amount);
        reimbursement.setReceiptUrl(receiptUrl);
        reimbursement.setStatus("Pending");
        reimbursement.setCreatedAt(LocalDateTime.now());

        reimbursementService.createReimbursement(reimbursement);

        return ResponseEntity.ok(reimbursement);
    }

    @GetMapping
    public List<ReimbursementModel> getAllReimbursements() {
        return reimbursementService.getAllReimbursements();
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getReimbursementsByEmployeeId(@PathVariable String employeeId) {
        try {
            List<ReimbursementModel> reimbursements = reimbursementService.getReimbursementsByEmployeeId(employeeId);
            return ResponseEntity.ok(reimbursements);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}