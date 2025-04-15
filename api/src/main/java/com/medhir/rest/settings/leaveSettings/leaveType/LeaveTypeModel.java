package com.medhir.rest.settings.leaveSettings.leaveType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "leave_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LeaveTypeModel {

    @Id
    @JsonIgnore
    private String id;

    private String leaveTypeId;

    @NotBlank(message = "Leave type name is required")
    private String leaveTypeName;

    @NotNull(message = "Company ID is required")
    private String companyId;

    @NotNull(message = "Accrual period is required")
    private String accrualPeriod;

    private String description;

    private boolean allowedInProbationPeriod;

    private boolean allowedInNoticePeriod;

    private boolean canBeCarriedForward;
}