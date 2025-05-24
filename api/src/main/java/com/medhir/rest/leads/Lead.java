package com.medhir.rest.leads;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import com.medhir.rest.utils.GeneratedId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.springframework.data.annotation.Transient;

@Getter
@Setter
@Document(collection = "leads")
@JsonInclude(JsonInclude.Include.ALWAYS)
public class Lead {
    @Id
    private String id;
    
    private String leadId;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @JsonProperty(required = false)
    @Indexed(unique = true)
    private String contactNumber = "";
    
    @JsonProperty(required = false)
    @Indexed(unique = true)
    private String email = "";
    
    @JsonProperty(required = false)
    private String projectType = "";
    
    @JsonProperty(required = false)
    private String projectAddress = "";
    
    @JsonProperty(required = false)
    private Double expectedBudget;
    
    @JsonProperty(required = false)
    private String status = "";
    
    @JsonProperty(required = false)
    private String salesRep = "";
    
    @JsonProperty(required = false)
    private String designer = "";
    
    @JsonProperty(required = false)
    private String callDescription = "";
    
    @JsonProperty(required = false)
    private List<String> callHistory;
    
    @JsonProperty(required = false)
    private String nextCall = "";
    
    @JsonProperty(required = false)
    private Double quotedAmount;
    
    @JsonProperty(required = false)
    private Double finalQuotation;
    
    @JsonProperty(required = false)
    private Double signupAmount;
    
    @JsonProperty(required = false)
    private String paymentDate = "";
    
    @JsonProperty(required = false)
    private String paymentMode = "";
    
    @JsonProperty(required = false)
    private String panNumber = "";
    
    @JsonProperty(required = false)
    private String projectTimeline = "";
    
    @JsonProperty(required = false)
    private Double discount;
    
    @JsonProperty(required = false)
    private String reasonForLost = "";
    
    @JsonProperty(required = false)
    private String reasonForJunk = "";
    
    @JsonProperty(required = false)
    private String submittedBy = "";
    
    @JsonProperty(required = false)
    private String paymentDetailsFileName = "";
    
    @JsonProperty(required = false)
    private String bookingFormFileName = "";

    @JsonIgnore
    @Transient
    private GeneratedId generatedId;

    @PostConstruct
    public void init() {
        if (contactNumber == null) contactNumber = "";
        if (email == null) email = "";
        if (projectType == null) projectType = "";
        if (projectAddress == null) projectAddress = "";
        if (status == null) status = "";
        if (salesRep == null) salesRep = "";
        if (designer == null) designer = "";
        if (callDescription == null) callDescription = "";
        if (nextCall == null) nextCall = "";
        if (paymentDate == null) paymentDate = "";
        if (paymentMode == null) paymentMode = "";
        if (panNumber == null) panNumber = "";
        if (projectTimeline == null) projectTimeline = "";
        if (reasonForLost == null) reasonForLost = "";
        if (reasonForJunk == null) reasonForJunk = "";
        if (submittedBy == null) submittedBy = "";
        if (paymentDetailsFileName == null) paymentDetailsFileName = "";
        if (bookingFormFileName == null) bookingFormFileName = "";
    }

    public void setGeneratedId(GeneratedId generatedId) {
        this.generatedId = generatedId;
    }

    public void generateLeadId() {
        if (this.leadId == null && this.generatedId != null) {
            this.leadId = generatedId.generateId("LEAD", Lead.class, "leadId");
        }
    }
}