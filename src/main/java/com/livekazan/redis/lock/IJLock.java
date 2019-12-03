package com.livekazan.redis.lock;

import java.time.ZonedDateTime;

/**
 * Lock representation interface for redis.
 */
public interface IJLock extends AutoCloseable {
    /**
     * Returns the key of this lock.
     */
    String getKey();

    /**
     * Returns the {@link ZonedDateTime} value with date and time when this lock was acquired.
     */
    ZonedDateTime getAcquiredAt();

    /**
     * Returns the timeout in milliseconds of this key.
     */
    Integer getTimeoutMs();
}
