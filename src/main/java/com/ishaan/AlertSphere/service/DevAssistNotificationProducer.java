package com.ishaan.AlertSphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishaan.AlertSphere.dto.DevAssistNotificationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DevAssistNotificationProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final String notificationTopic;

    public DevAssistNotificationProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            @Value("${alerts.kafka.devassist-notification-topic:notification-events}") String notificationTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.notificationTopic = notificationTopic;
    }

    public void publish(DevAssistNotificationEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(notificationTopic, event.getIncidentId().toString(), payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to publish DevAssist notification callback for incident {}", event.getIncidentId(), exception);
                            return;
                        }
                        log.info("Published DevAssist notification callback incidentId={} status={}",
                                event.getIncidentId(), event.getDeliveryStatus());
                    });
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize DevAssist notification callback", exception);
        }
    }
}
