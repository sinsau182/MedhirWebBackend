package com.medhir.rest.income;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import com.medhir.rest.utils.GeneratedId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.springframework.data.annotation.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@Document(collection = "income")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Income {
    @Id
    private String id;
    
    private String incomeId;
    
    @NotBlank(message = "Project is required")
    @JsonProperty(required = true)
    private String project;
    
    @NotBlank(message = "Client is required")
    @JsonProperty(required = true)
    private String client;
    
    @NotNull(message = "Amount is required")
    @JsonProperty(required = true)
    private Double amount;
    
    @NotBlank(message = "Initiated date is required")
    @JsonProperty(required = true)
    private String initiated;
    
    @NotBlank(message = "Status is required")
    @JsonProperty(required = true)
    private String status;
    
    // @JsonProperty(required = false)
    // private String category = "";
    
    @NotBlank(message = "File is required")
    @JsonProperty(required = true)
    private String file = "";
    
    @JsonProperty(required = false)
    private String comments = "";
    
    @NotBlank(message = "Submitted by (employeeId) is required")
    @JsonProperty(required = true)
    @Indexed
    private String submittedBy = "";

    @JsonIgnore
    @Transient
    private GeneratedId generatedId;

    @PostConstruct
    public void init() {
        if (comments == null) comments = "";
        if (file == null) file = "";
        if (submittedBy == null) submittedBy = "";
    }

    public void setGeneratedId(GeneratedId generatedId) {
        this.generatedId = generatedId;
    }

    public void generateIncomeId() {
        if (this.incomeId == null && this.generatedId != null) {
            this.incomeId = generatedId.generateId("INC", Income.class, "incomeId");
        }
    }
} 