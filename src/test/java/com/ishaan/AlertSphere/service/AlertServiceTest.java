package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.dto.WeatherSnapshot;
import com.ishaan.AlertSphere.entity.Alert;
import com.ishaan.AlertSphere.enums.AlertConditionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AlertServiceTest {

    private final AlertService alertService = new AlertService();

    @Test
    void shouldTriggerWhenAllAndConditionsMatch() {
        Alert alert = Alert.builder()
                .active(true)
                .conditionType(AlertConditionType.AND)
                .temperatureAbove(30.0)
                .humidityBelow(80)
                .weatherCondition("Rain")
                .build();

        assertThat(alertService.shouldTrigger(alert, weather(32.0, 60, 12.0, "rain"))).isTrue();
    }

    @Test
    void shouldNotTriggerWhenAnyAndConditionFails() {
        Alert alert = Alert.builder()
                .active(true)
                .conditionType(AlertConditionType.AND)
                .temperatureAbove(30.0)
                .humidityBelow(50)
                .build();

        assertThat(alertService.shouldTrigger(alert, weather(32.0, 60, 12.0, "Clear"))).isFalse();
    }

    @Test
    void shouldTriggerWhenAnyOrConditionMatches() {
        Alert alert = Alert.builder()
                .active(true)
                .conditionType(AlertConditionType.OR)
                .temperatureAbove(40.0)
                .windSpeedAbove(20.0)
                .weatherCondition("Clouds")
                .build();

        assertThat(alertService.shouldTrigger(alert, weather(32.0, 12, 8.0, "Clouds"))).isTrue();
    }

    @Test
    void shouldNotTriggerDuringCooldown() {
        Alert alert = Alert.builder()
                .active(true)
                .conditionType(AlertConditionType.OR)
                .temperatureAbove(30.0)
                .cooldownMinutes(30)
                .lastTriggeredAt(LocalDateTime.now().minusMinutes(5))
                .build();

        assertThat(alertService.shouldTrigger(alert, weather(32.0, 60, 12.0, "Clear"))).isFalse();
    }

    private WeatherSnapshot weather(double temperature, int humidity, double windSpeed, String condition) {
        WeatherSnapshot weather = new WeatherSnapshot();
        weather.setCity("Delhi");
        weather.setTemperature(temperature);
        weather.setHumidity(humidity);
        weather.setWindSpeed(windSpeed);
        weather.setWeatherCondition(condition);
        return weather;
    }
}
