package com.ishaan.AlertSphere.service;

import com.ishaan.AlertSphere.entity.Alert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
public class AlertCooldownService {

    private static final int DEFAULT_COOLDOWN_MINUTES = 30;

    private final StringRedisTemplate stringRedisTemplate;

    public AlertCooldownService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public boolean acquireCooldownLock(Alert alert) {
        if (alert.getId() == null) {
            return false;
        }

        int cooldownMinutes = alert.getCooldownMinutes() == null
                ? DEFAULT_COOLDOWN_MINUTES
                : alert.getCooldownMinutes();

        try {
            Boolean locked = stringRedisTemplate.opsForValue()
                    .setIfAbsent(key(alert), "1", Duration.ofMinutes(cooldownMinutes));
            return Boolean.TRUE.equals(locked);
        } catch (RedisConnectionFailureException e) {
            log.warn("Redis unavailable while creating cooldown lock for alert {}", alert.getId());
            return true;
        }
    }

    private String key(Alert alert) {
        return "alert:cooldown:" + alert.getId().toHexString();
    }
}
