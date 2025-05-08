package com.medhir.rest.reimbursements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.medhir.rest.reimbursements.ReimbursementModel;
import com.medhir.rest.reimbursements.ReimbursementRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReimbursementService {

    @Autowired
    private ReimbursementRepository reimbursementRepository;

    public ReimbursementModel createReimbursement(ReimbursementModel reimbursement) {
        reimbursement.setStatus("Pending");
        reimbursement.setCreatedAt(LocalDateTime.now());
        return reimbursementRepository.save(reimbursement);
    }

    public List<ReimbursementModel> getAllReimbursements() {
        List<ReimbursementModel> reimbursements = reimbursementRepository.findAll();
        // Always show status as Pending
        reimbursements.forEach(r -> r.setStatus("Pending"));
        return reimbursements;
    }
}
