package com.uzh.ase.dailygrind.postservice.post.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TimeToLiveHelper {

    @Value("${post.ttlMinutes:false}")
    private static boolean ttlMinutes;

    public static long getTimeToLive() {
        if (ttlMinutes) {
            return Instant.now()
                .plus(Duration.ofMinutes(1))
                .getEpochSecond();
        } else {
            // Tomorrow 1pm
            // Tomorrow 1 PM UTC
            return LocalDateTime.now(ZoneOffset.UTC)
                .plusDays(1)
                .withHour(13)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                .toEpochSecond(ZoneOffset.UTC);
        }
    }

}
