package com.ishaan.AlertSphere.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class WeatherSnapshot {

    @NotBlank
    private String city;

    @NotNull
    private Double temperature;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer humidity;

    @NotNull
    @PositiveOrZero
    private Double windSpeed;

    @NotBlank
    private String weatherCondition;
}
