package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.dto.WeatherAlertEmailEvent;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

@Service
public class AlertEmailDeliveryService {

    private final EmailNotificationService emailNotificationService;
    private final AlertService alertService;

    public AlertEmailDeliveryService(
            EmailNotificationService emailNotificationService,
            AlertService alertService
    ) {
        this.emailNotificationService = emailNotificationService;
        this.alertService = alertService;
    }

    public void sendAndMarkTriggered(WeatherAlertEmailEvent event) {
        emailNotificationService.sendWeatherAlert(event);

        if (ObjectId.isValid(event.getAlertId())) {
            alertService.getAlert(new ObjectId(event.getAlertId()))
                    .ifPresent(alertService::markTriggered);
        }
    }
}
