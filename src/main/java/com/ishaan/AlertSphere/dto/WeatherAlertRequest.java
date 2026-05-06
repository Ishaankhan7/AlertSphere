package com.ishaan.AlertSphere.dto;

import com.ishaan.AlertSphere.enums.AlertConditionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WeatherAlertRequest {

    @NotBlank
    @Size(max = 100)
    private String city;

    private Double temperatureAbove;
    private Double temperatureBelow;

    @Min(0)
    @Max(100)
    private Integer humidityAbove;

    @Min(0)
    @Max(100)
    private Integer humidityBelow;

    @PositiveOrZero
    private Double windSpeedAbove;

    @Size(max = 50)
    private String weatherCondition;

    private Boolean active;
    private AlertConditionType conditionType;

    @Min(0)
    private Integer cooldownMinutes;

    @AssertTrue(message = "At least one weather condition must be defined")
    public boolean hasAtLeastOneCondition() {
        return temperatureAbove != null
                || temperatureBelow != null
                || humidityAbove != null
                || humidityBelow != null
                || windSpeedAbove != null
                || (weatherCondition != null && !weatherCondition.isBlank());
    }

    @AssertTrue(message = "temperatureAbove must be greater than temperatureBelow")
    public boolean hasValidTemperatureRange() {
        return temperatureAbove == null
                || temperatureBelow == null
                || temperatureAbove > temperatureBelow;
    }

    @AssertTrue(message = "humidityAbove must be greater than humidityBelow")
    public boolean hasValidHumidityRange() {
        return humidityAbove == null
                || humidityBelow == null
                || humidityAbove > humidityBelow;
    }
}
