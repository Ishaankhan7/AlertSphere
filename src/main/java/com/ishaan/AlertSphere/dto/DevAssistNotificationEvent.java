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
public class DevAssistNotificationEvent {

    private String eventId;
    private Long incidentId;
    private String channel;
    private String deliveryStatus;
    private String message;
    private Instant occurredAt;
}
