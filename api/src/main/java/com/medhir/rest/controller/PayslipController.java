package com.medhir.rest.controller;

import com.medhir.rest.payslip.PayslipModel;
import com.medhir.rest.payslip.PayslipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payslip")
public class PayslipController {

    @Autowired
    private PayslipService payslipService;

    @GetMapping("/generate/{employeeId}/{month}/{year}")
    public ResponseEntity<PayslipModel> generatePayslip(
            @PathVariable String employeeId,
            @PathVariable String month,
            @PathVariable int year
          ) {
        
        PayslipModel payslip = payslipService.generatePayslip(employeeId, month, year);
        return ResponseEntity.ok(payslip);
    }
} 