package com.medhir.rest.updates;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "updates")
public class UpdatesModel {

    @Id
    private String id;
    private String employeeId;
    private String message;
    private String flag; // To show different icons at the frontend

    // Store in UTC but return in IST
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Kolkata")
    private LocalDateTime timestamp;

    // Convert IST to UTC before saving
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = ZonedDateTime.of(timestamp, ZoneId.of("Asia/Kolkata"))
                .withZoneSameInstant(ZoneId.of("UTC"))
                .toLocalDateTime();
    }
}
