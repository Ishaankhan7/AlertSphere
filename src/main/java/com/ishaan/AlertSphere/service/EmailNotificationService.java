package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.dto.DevAssistIncidentEvent;
import com.ishaan.AlertSphere.dto.WeatherAlertEmailEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailNotificationService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:no-reply@alertsphere.local}") String fromEmail
    ) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    public void sendWeatherAlert(WeatherAlertEmailEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.getUserEmail());
        message.setSubject("Weather alert for " + event.getCity());
        message.setText(buildBody(event));
        mailSender.send(message);
    }

    public void sendDevAssistIncidentAlert(DevAssistIncidentEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.getUserEmail());
        message.setSubject("DevAssist incident: " + event.getApiName() + " is " + event.getTriggerStatus());
        message.setText(buildDevAssistIncidentBody(event));
        mailSender.send(message);
    }

    private String buildBody(WeatherAlertEmailEvent event) {
        return """
                Your weather alert condition matched.

                City: %s
                Temperature: %.1f C
                Humidity: %d%%
                Wind speed: %.1f m/s
                Condition: %s
                Triggered at: %s
                """.formatted(
                event.getCity(),
                event.getTemperature(),
                event.getHumidity(),
                event.getWindSpeed(),
                event.getWeatherCondition(),
                event.getTriggeredAt()
        );
    }

    private String buildDevAssistIncidentBody(DevAssistIncidentEvent event) {
        String displayName = event.getUserName() == null || event.getUserName().isBlank()
                ? "there"
                : event.getUserName();

        return """
                Hi %s,

                DevAssist detected an incident for one of your monitored APIs.

                API: %s
                API ID: %s
                Incident ID: %s
                Status: %s
                Message: %s
                Occurred at: %s
                """.formatted(
                displayName,
                event.getApiName(),
                event.getApiId(),
                event.getIncidentId(),
                event.getTriggerStatus(),
                event.getMessage(),
                event.getOccurredAt()
        );
    }
}
