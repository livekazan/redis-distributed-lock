package com.livekazan.redis.lock.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import org.springframework.stereotype.Component;

@Component
public class RedisDateUtils implements IRedisDateUtils {

    @Override
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    }

    @Override
    public long getDifference(Temporal d1, Temporal d2, ChronoUnit unit) {
        if (d1 == null || d2 == null) {
            return 0L;
        }
        return unit.between(d1, d2);
    }
}
