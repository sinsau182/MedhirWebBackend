package com.medhir.rest.leads;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.medhir.rest.utils.GeneratedId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class LeadService {
    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private GeneratedId generatedId;

    public Lead createLead(Lead lead) {
        // Check if lead with same name exists
        if (leadRepository.existsByContactNumber(lead.getContactNumber())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A lead with contact number '" + lead.getContactNumber() + "' already exists");
        }
        if (leadRepository.existsByEmail(lead.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A lead with email '" + lead.getEmail() + "' already exists");
        }

        try {
            lead.setGeneratedId(generatedId);
            lead.generateLeadId();
            return leadRepository.save(lead);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A lead with this contact number or email already exists");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error creating lead: " + e.getMessage());
        }
    }

    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    public Optional<Lead> getLeadById(String leadId) {
        return leadRepository.findByLeadId(leadId);
    }

    public Lead updateLead(String leadId, Lead lead) {
        Optional<Lead> existingLead = leadRepository.findByLeadId(leadId);
        if (existingLead.isPresent()) {
            Lead currentLead = existingLead.get();
            
            // Check for duplicate contact number if it's being changed
            if (lead.getContactNumber() != null && !lead.getContactNumber().isEmpty() && 
                !lead.getContactNumber().equals(currentLead.getContactNumber()) && 
                leadRepository.existsByContactNumber(lead.getContactNumber())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A lead with contact number '" + lead.getContactNumber() + "' already exists");
            }
            
            // Check for duplicate email if it's being changed
            if (lead.getEmail() != null && !lead.getEmail().isEmpty() && 
                !lead.getEmail().equals(currentLead.getEmail()) && 
                leadRepository.existsByEmail(lead.getEmail())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "A lead with email '" + lead.getEmail() + "' already exists");
            }
            
            // Preserve existing values if not provided in update
            if (lead.getName() == null) lead.setName(currentLead.getName());
            if (lead.getContactNumber() == "") lead.setContactNumber(currentLead.getContactNumber());
            if (lead.getEmail() == "") lead.setEmail(currentLead.getEmail());
            if (lead.getProjectType() == "") lead.setProjectType(currentLead.getProjectType());
            if (lead.getProjectAddress() == "") lead.setProjectAddress(currentLead.getProjectAddress());
            if (lead.getExpectedBudget() == null) lead.setExpectedBudget(currentLead.getExpectedBudget());
            if (lead.getStatus() == "") lead.setStatus(currentLead.getStatus());
            if (lead.getSalesRep() == "") lead.setSalesRep(currentLead.getSalesRep());
            if (lead.getDesigner() == "") lead.setDesigner(currentLead.getDesigner());
            if (lead.getCallDescription() == "") lead.setCallDescription(currentLead.getCallDescription());
            if (lead.getCallHistory() == null) lead.setCallHistory(currentLead.getCallHistory());
            if (lead.getNextCall() == "") lead.setNextCall(currentLead.getNextCall());
            if (lead.getQuotedAmount() == null) lead.setQuotedAmount(currentLead.getQuotedAmount());
            if (lead.getFinalQuotation() == null) lead.setFinalQuotation(currentLead.getFinalQuotation());
            if (lead.getSignupAmount() == null) lead.setSignupAmount(currentLead.getSignupAmount());
            if (lead.getPaymentDate() == "") lead.setPaymentDate(currentLead.getPaymentDate());
            if (lead.getPaymentMode() == "") lead.setPaymentMode(currentLead.getPaymentMode());
            if (lead.getPanNumber() == "") lead.setPanNumber(currentLead.getPanNumber());
            if (lead.getProjectTimeline() == "") lead.setProjectTimeline(currentLead.getProjectTimeline());
            if (lead.getDiscount() == null) lead.setDiscount(currentLead.getDiscount());
            if (lead.getReasonForLost() == "") lead.setReasonForLost(currentLead.getReasonForLost());
            if (lead.getReasonForJunk() == "") lead.setReasonForJunk(currentLead.getReasonForJunk());
            if (lead.getSubmittedBy() == null) lead.setSubmittedBy(currentLead.getSubmittedBy());
            if (lead.getPaymentDetailsFileName() == "") lead.setPaymentDetailsFileName(currentLead.getPaymentDetailsFileName());
            if (lead.getBookingFormFileName() == "") lead.setBookingFormFileName(currentLead.getBookingFormFileName());
            
            // Preserve the original leadId and id
            lead.setLeadId(leadId);
            lead.setId(currentLead.getId());
            return leadRepository.save(lead);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with ID '" + leadId + "' not found");
        }
    }

    public void deleteLead(String leadId) {
        Optional<Lead> lead = leadRepository.findByLeadId(leadId);
        if (lead.isPresent()) {
            try {
                leadRepository.delete(lead.get());
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting lead: " + e.getMessage());
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lead with ID '" + leadId + "' not found");
        }
    }

    // Filtering can be implemented with custom queries or specifications as needed
} 