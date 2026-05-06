package com.ishaan.AlertSphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ishaan.AlertSphere.dto.WeatherSnapshot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Locale;

@Slf4j
@Service
public class WeatherCacheService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final WeatherService weatherService;
    private final Duration ttl;

    public WeatherCacheService(
            StringRedisTemplate stringRedisTemplate,
            ObjectMapper objectMapper,
            WeatherService weatherService,
            @Value("${weather.cache.ttl-minutes:5}") long ttlMinutes
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.weatherService = weatherService;
        this.ttl = Duration.ofMinutes(ttlMinutes);
    }

    public WeatherSnapshot getCurrentWeather(String city) {
        String key = key(city);

        try {
            String cachedWeather = stringRedisTemplate.opsForValue().get(key);
            if (cachedWeather != null) {
                return objectMapper.readValue(cachedWeather, WeatherSnapshot.class);
            }
        } catch (RedisConnectionFailureException | JsonProcessingException e) {
            log.warn("Redis unavailable while reading weather cache for {}", city);
        }

        WeatherSnapshot freshWeather = weatherService.getCurrentWeather(city);

        try {
            stringRedisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(freshWeather), ttl);
        } catch (RedisConnectionFailureException | JsonProcessingException e) {
            log.warn("Redis unavailable while writing weather cache for {}", city);
        }

        return freshWeather;
    }

    private String key(String city) {
        return "weather:" + city.trim().toLowerCase(Locale.ROOT);
    }
}
