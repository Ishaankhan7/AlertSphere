package com.ishaan.AlertSphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishaan.AlertSphere.dto.DevAssistIncidentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "alerts.devassist.consumer.enabled", havingValue = "true", matchIfMissing = true)
public class DevAssistIncidentConsumer {

    private final ObjectMapper objectMapper;
    private final DevAssistIncidentNotificationService notificationService;

    public DevAssistIncidentConsumer(
            ObjectMapper objectMapper,
            DevAssistIncidentNotificationService notificationService
    ) {
        this.objectMapper = objectMapper;
        this.notificationService = notificationService;
    }

    @KafkaListener(
            topics = "${alerts.kafka.devassist-incident-topic:incident-events}",
            groupId = "${alerts.kafka.devassist-consumer-group:alert-sphere-devassist}"
    )
    public void consume(String payload) {
        DevAssistIncidentEvent event = parse(payload);
        log.info("Received DevAssist incident event incidentId={} apiId={} status={}",
                event.getIncidentId(), event.getApiId(), event.getTriggerStatus());
        notificationService.notifyIncident(event);
    }

    private DevAssistIncidentEvent parse(String payload) {
        try {
            return objectMapper.readValue(payload, DevAssistIncidentEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid DevAssist incident event payload", exception);
        }
    }
}
