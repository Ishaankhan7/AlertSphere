package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.dto.DevAssistIncidentEvent;
import com.ishaan.AlertSphere.dto.DevAssistNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class DevAssistIncidentNotificationService {

    private static final String CHANNEL_EMAIL = "EMAIL";
    private static final String STATUS_SENT = "SENT";
    private static final String STATUS_FAILED = "FAILED";

    private final EmailNotificationService emailNotificationService;
    private final DevAssistNotificationProducer notificationProducer;

    public DevAssistIncidentNotificationService(
            EmailNotificationService emailNotificationService,
            DevAssistNotificationProducer notificationProducer
    ) {
        this.emailNotificationService = emailNotificationService;
        this.notificationProducer = notificationProducer;
    }

    public void notifyIncident(DevAssistIncidentEvent event) {
        if (event.getUserEmail() == null || event.getUserEmail().isBlank()) {
            publishCallback(event, STATUS_FAILED, "DevAssist incident event did not include a recipient email");
            log.warn("Skipping DevAssist incident notification because userEmail is missing incidentId={}", event.getIncidentId());
            return;
        }

        try {
            emailNotificationService.sendDevAssistIncidentAlert(event);
            publishCallback(event, STATUS_SENT, "DevAssist incident email sent");
            log.info("Sent DevAssist incident email incidentId={} apiId={} recipient={}",
                    event.getIncidentId(), event.getApiId(), event.getUserEmail());
        } catch (RuntimeException exception) {
            publishCallback(event, STATUS_FAILED, exception.getMessage());
            log.error("Failed to send DevAssist incident email incidentId={} apiId={}",
                    event.getIncidentId(), event.getApiId(), exception);
        }
    }

    private void publishCallback(DevAssistIncidentEvent incidentEvent, String deliveryStatus, String message) {
        DevAssistNotificationEvent callback = new DevAssistNotificationEvent(
                UUID.randomUUID().toString(),
                incidentEvent.getIncidentId(),
                CHANNEL_EMAIL,
                deliveryStatus,
                message,
                Instant.now()
        );
        notificationProducer.publish(callback);
    }
}
