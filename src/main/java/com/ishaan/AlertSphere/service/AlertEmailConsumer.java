package com.ishaan.AlertSphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishaan.AlertSphere.dto.WeatherAlertEmailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "alerts.kafka.consumer.enabled", havingValue = "true", matchIfMissing = true)
public class AlertEmailConsumer {

    private final ObjectMapper objectMapper;
    private final AlertEmailDeliveryService alertEmailDeliveryService;

    public AlertEmailConsumer(
            ObjectMapper objectMapper,
            AlertEmailDeliveryService alertEmailDeliveryService
    ) {
        this.objectMapper = objectMapper;
        this.alertEmailDeliveryService = alertEmailDeliveryService;
    }

    @KafkaListener(
            topics = "${alerts.kafka.email-topic:weather-alert-email}",
            groupId = "${spring.kafka.consumer.group-id:alert-sphere-mail}"
    )
    public void consume(String payload) {
        WeatherAlertEmailEvent event = parse(payload);
        alertEmailDeliveryService.sendAndMarkTriggered(event);
    }

    private WeatherAlertEmailEvent parse(String payload) {
        try {
            return objectMapper.readValue(payload, WeatherAlertEmailEvent.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid weather alert email event payload", e);
        }
    }
}
