package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.dto.WeatherAlertRequest;
import com.ishaan.AlertSphere.dto.WeatherSnapshot;
import com.ishaan.AlertSphere.entity.Alert;
import com.ishaan.AlertSphere.entity.User;
import com.ishaan.AlertSphere.enums.AlertConditionType;
import com.ishaan.AlertSphere.repository.AlertRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private static final int DEFAULT_COOLDOWN_MINUTES = 30;

    @Autowired
    private AlertRepository alertRepository;

    public Alert createAlert(WeatherAlertRequest request, User user) {
        LocalDateTime now = LocalDateTime.now();
        Alert alert = Alert.builder()
                .userId(user.getId().toHexString())
                .userEmail(user.getEmail())
                .city(normalize(request.getCity()))
                .temperatureAbove(request.getTemperatureAbove())
                .temperatureBelow(request.getTemperatureBelow())
                .humidityAbove(request.getHumidityAbove())
                .humidityBelow(request.getHumidityBelow())
                .windSpeedAbove(request.getWindSpeedAbove())
                .weatherCondition(normalizeNullable(request.getWeatherCondition()))
                .active(request.getActive() == null || request.getActive())
                .conditionType(request.getConditionType() == null ? AlertConditionType.AND : request.getConditionType())
                .cooldownMinutes(request.getCooldownMinutes() == null ? DEFAULT_COOLDOWN_MINUTES : request.getCooldownMinutes())
                .createdAt(now)
                .updatedAt(now)
                .build();

        return alertRepository.save(alert);
    }

    public List<Alert> getUserAlerts(User user) {
        return alertRepository.findByUserId(user.getId().toHexString());
    }

    public Optional<Alert> getUserAlert(ObjectId id, User user) {
        return alertRepository.findByIdAndUserId(id, user.getId().toHexString());
    }

    public Optional<Alert> getAlert(ObjectId id) {
        return alertRepository.findById(id);
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByActiveTrue();
    }

    public Optional<Alert> updateAlert(ObjectId id, WeatherAlertRequest request, User user) {
        return getUserAlert(id, user)
                .map(alert -> {
                    alert.setCity(normalize(request.getCity()));
                    alert.setTemperatureAbove(request.getTemperatureAbove());
                    alert.setTemperatureBelow(request.getTemperatureBelow());
                    alert.setHumidityAbove(request.getHumidityAbove());
                    alert.setHumidityBelow(request.getHumidityBelow());
                    alert.setWindSpeedAbove(request.getWindSpeedAbove());
                    alert.setWeatherCondition(normalizeNullable(request.getWeatherCondition()));
                    alert.setActive(request.getActive() == null || request.getActive());
                    alert.setConditionType(request.getConditionType() == null ? AlertConditionType.AND : request.getConditionType());
                    alert.setCooldownMinutes(request.getCooldownMinutes() == null ? DEFAULT_COOLDOWN_MINUTES : request.getCooldownMinutes());
                    alert.setUpdatedAt(LocalDateTime.now());
                    return alertRepository.save(alert);
                });
    }

    public boolean delete(ObjectId id, User user) {
        return alertRepository.findByIdAndUserId(id, user.getId().toHexString())
                .map(alert -> {
                    alertRepository.delete(alert);
                    return true;
                })
                .orElse(false);
    }

    public List<Alert> findTriggeredAlerts(WeatherSnapshot weather) {
        List<Alert> activeAlerts = alertRepository.findByCityIgnoreCaseAndActiveTrue(weather.getCity());
        return activeAlerts.stream()
                .filter(alert -> shouldTrigger(alert, weather))
                .toList();
    }

    public boolean shouldTrigger(Alert alert, WeatherSnapshot weather) {
        if (!Boolean.TRUE.equals(alert.getActive()) || isInCooldown(alert)) {
            return false;
        }

        List<Boolean> checks = buildChecks(alert, weather);
        if (checks.isEmpty()) {
            return false;
        }

        if (alert.getConditionType() == AlertConditionType.OR) {
            return checks.stream().anyMatch(Boolean::booleanValue);
        }

        return checks.stream().allMatch(Boolean::booleanValue);
    }

    public Alert markTriggered(Alert alert) {
        alert.setLastTriggeredAt(LocalDateTime.now());
        alert.setUpdatedAt(LocalDateTime.now());
        return alertRepository.save(alert);
    }

    private boolean isInCooldown(Alert alert) {
        if (alert.getLastTriggeredAt() == null) {
            return false;
        }

        int cooldownMinutes = alert.getCooldownMinutes() == null ? DEFAULT_COOLDOWN_MINUTES : alert.getCooldownMinutes();
        return alert.getLastTriggeredAt()
                .plusMinutes(cooldownMinutes)
                .isAfter(LocalDateTime.now());
    }

    private List<Boolean> buildChecks(Alert alert, WeatherSnapshot weather) {
        List<Boolean> checks = new ArrayList<>();

        if (alert.getTemperatureAbove() != null) {
            checks.add(weather.getTemperature() > alert.getTemperatureAbove());
        }
        if (alert.getTemperatureBelow() != null) {
            checks.add(weather.getTemperature() < alert.getTemperatureBelow());
        }
        if (alert.getHumidityAbove() != null) {
            checks.add(weather.getHumidity() > alert.getHumidityAbove());
        }
        if (alert.getHumidityBelow() != null) {
            checks.add(weather.getHumidity() < alert.getHumidityBelow());
        }
        if (alert.getWindSpeedAbove() != null) {
            checks.add(weather.getWindSpeed() > alert.getWindSpeedAbove());
        }
        if (alert.getWeatherCondition() != null && !alert.getWeatherCondition().isBlank()) {
            checks.add(alert.getWeatherCondition().equalsIgnoreCase(weather.getWeatherCondition()));
        }

        return checks;
    }

    private String normalize(String value) {
        return value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
