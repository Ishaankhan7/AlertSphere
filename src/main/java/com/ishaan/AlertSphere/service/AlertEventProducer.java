package com.ishaan.AlertSphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishaan.AlertSphere.dto.WeatherAlertEmailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AlertEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final AlertEmailDeliveryService alertEmailDeliveryService;
    private final String emailTopic;

    public AlertEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper,
            AlertEmailDeliveryService alertEmailDeliveryService,
            @Value("${alerts.kafka.email-topic:weather-alert-email}") String emailTopic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.alertEmailDeliveryService = alertEmailDeliveryService;
        this.emailTopic = emailTopic;
    }

    public void publishEmailEvent(WeatherAlertEmailEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(emailTopic, event.getAlertId(), payload)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error("Failed to publish weather alert email event for alert {}", event.getAlertId(), exception);
                            sendDirectly(event);
                            return;
                        }
                        log.info("Published weather alert email event for alert {}", event.getAlertId());
                    });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize weather alert email event", e);
        } catch (RuntimeException e) {
            log.error("Kafka unavailable while publishing weather alert email event for alert {}", event.getAlertId(), e);
            sendDirectly(event);
        }
    }

    private void sendDirectly(WeatherAlertEmailEvent event) {
        try {
            alertEmailDeliveryService.sendAndMarkTriggered(event);
            log.info("Sent weather alert email directly for alert {}", event.getAlertId());
        } catch (RuntimeException e) {
            log.error("Kafka fallback failed while sending weather alert email for alert {}", event.getAlertId(), e);
        }
    }
}
