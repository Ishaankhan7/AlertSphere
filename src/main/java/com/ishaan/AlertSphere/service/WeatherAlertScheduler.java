package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.dto.WeatherAlertEmailEvent;
import com.ishaan.AlertSphere.dto.WeatherSnapshot;
import com.ishaan.AlertSphere.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@ConditionalOnProperty(name = "alerts.weather-check.enabled", havingValue = "true", matchIfMissing = true)
public class WeatherAlertScheduler {

    private final AlertService alertService;
    private final WeatherCacheService weatherCacheService;
    private final AlertCooldownService alertCooldownService;
    private final AlertEventProducer alertEventProducer;

    public WeatherAlertScheduler(
            AlertService alertService,
            WeatherCacheService weatherCacheService,
            AlertCooldownService alertCooldownService,
            AlertEventProducer alertEventProducer
    ) {
        this.alertService = alertService;
        this.weatherCacheService = weatherCacheService;
        this.alertCooldownService = alertCooldownService;
        this.alertEventProducer = alertEventProducer;
    }

    @Scheduled(fixedDelayString = "${alerts.weather-check.delay-ms:300000}")
    public void checkWeatherAlerts() {
        List<Alert> activeAlerts = alertService.getActiveAlerts();
        if (activeAlerts.isEmpty()) {
            return;
        }

        Map<String, String> cities = activeAlerts.stream()
                .collect(Collectors.toMap(
                        alert -> alert.getCity().toLowerCase(Locale.ROOT),
                        Alert::getCity,
                        (first, ignored) -> first
                ));

        cities.values().forEach(this::checkCity);
    }

    private void checkCity(String city) {
        try {
            WeatherSnapshot weather = weatherCacheService.getCurrentWeather(city);
            List<Alert> triggeredAlerts = alertService.findTriggeredAlerts(weather);

            triggeredAlerts.stream()
                    .filter(alertCooldownService::acquireCooldownLock)
                    .map(alert -> toEmailEvent(alert, weather))
                    .forEach(alertEventProducer::publishEmailEvent);
        } catch (Exception e) {
            log.error("Failed to check weather alerts for city {}", city, e);
        }
    }

    private WeatherAlertEmailEvent toEmailEvent(Alert alert, WeatherSnapshot weather) {
        return new WeatherAlertEmailEvent(
                alert.getId().toHexString(),
                alert.getUserEmail(),
                weather.getCity(),
                weather.getTemperature(),
                weather.getHumidity(),
                weather.getWindSpeed(),
                weather.getWeatherCondition(),
                LocalDateTime.now()
        );
    }
}
