package com.medhir.rest.settings.payrollSettings.professionalTax;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "professional_tax_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalTaxSettings {

    @Id
    @JsonIgnore
    private String id;

    @NotBlank(message = "company Id cannot be empty")
    private String companyId;
    private String professionalTaxId = "PT1";
    private Double monthlySalaryThreshold;
    private Double amountAboveThreshold;
    private Double amountBelowThreshold;
    private String description;
    private String createdAt;
    private String updatedAt;
}