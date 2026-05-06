package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.dto.WeatherSnapshot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class WeatherService {

    private final RestClient weatherRestClient;
    private final String apiUrl;
    private final String apiKey;

    public WeatherService(
            RestClient weatherRestClient,
            @Value("${weather.api.url}") String apiUrl,
            @Value("${weather.api.key:}") String apiKey
    ) {
        this.weatherRestClient = weatherRestClient;
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    public WeatherSnapshot getCurrentWeather(String city) {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("weather.api.key is not configured");
        }

        URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .build()
                .toUri();

        OpenWeatherResponse response = weatherRestClient.get()
                .uri(uri)
                .retrieve()
                .body(OpenWeatherResponse.class);

        if (response == null || response.main() == null || response.wind() == null) {
            throw new IllegalStateException("Weather API returned an invalid response for city: " + city);
        }

        WeatherSnapshot weather = new WeatherSnapshot();
        weather.setCity(StringUtils.hasText(response.name()) ? response.name() : city);
        weather.setTemperature(response.main().temp());
        weather.setHumidity(response.main().humidity());
        weather.setWindSpeed(response.wind().speed());
        weather.setWeatherCondition(resolveCondition(response.weather()));
        return weather;
    }

    private String resolveCondition(List<OpenWeatherCondition> weather) {
        if (weather == null || weather.isEmpty() || !StringUtils.hasText(weather.get(0).main())) {
            return "Unknown";
        }

        return weather.get(0).main();
    }

    private record OpenWeatherResponse(
            String name,
            OpenWeatherMain main,
            OpenWeatherWind wind,
            List<OpenWeatherCondition> weather
    ) {
    }

    private record OpenWeatherMain(Double temp, Integer humidity) {
    }

    private record OpenWeatherWind(Double speed) {
    }

    private record OpenWeatherCondition(String main) {
    }
}
