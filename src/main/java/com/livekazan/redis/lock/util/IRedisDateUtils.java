package com.livekazan.redis.lock.util;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public interface IRedisDateUtils {

    ZonedDateTime getZonedDateTime();

    long getDifference(Temporal d1, Temporal d2, ChronoUnit unit);
}
