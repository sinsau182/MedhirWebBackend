package com.medhir.rest.leads;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class LeadFilterDTO {
    private String leadId;
    private String leadBy;
    private String name;
    private String email;
    private String contact;
    private String projectType;
    private String projectAddress;
    private Double budget;
    private Double initialQuote;
    private Double finalQuote;
    private Double signUpAmount;
    private String paymentDate;
    private String paymentMode;
    private String PAN;
    private String timeline;
    private List<String> salesRepresentative;
    private List<String> designer;
    private Double discount;
    private List<String> paymentDocs;
    private String bookingForm;
    private List<String> latestCallDescription;
    private String nextFollowUp;
    private Boolean isLost;
    private String lostReason;
    private Boolean isJunk;
    private String junkReason;
    private String status;
} 