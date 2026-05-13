package com.ishaan.AlertSphere.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DevAssistIncidentEvent {

    private String eventId;
    private Long incidentId;
    private Long apiId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String apiName;
    private String triggerStatus;
    private String message;
    private Instant occurredAt;
}
