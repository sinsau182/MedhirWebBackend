package com.medhir.rest.settings.leaveSettings.publicHolidays;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "public_holidays")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicHolidayModel {

    @Id
    @JsonIgnore
    private String id;

    private String holidayId;

    @NotBlank(message = "Holiday name is required")
    private String holidayName;

    @NotBlank(message = "Company ID is required")
    private String companyId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String description;
}