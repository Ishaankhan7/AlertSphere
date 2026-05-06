package com.ishaan.AlertSphere.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherAlertEmailEvent {

    private String alertId;
    private String userEmail;
    private String city;
    private Double temperature;
    private Integer humidity;
    private Double windSpeed;
    private String weatherCondition;
    private LocalDateTime triggeredAt;
}
