package com.livekazan.redis.lock.impl;

import com.livekazan.redis.lock.IJLock;
import com.livekazan.redis.lock.ILockService;
import java.time.ZonedDateTime;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * Lock representation for redis.
 */
@Data
@EqualsAndHashCode
@ToString(of = {"key", "acquiredAt", "timeoutMs"})
@Slf4j
public final class JLock implements IJLock {
    private String key;
    private ZonedDateTime acquiredAt;
    private Integer timeoutMs;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private ILockService lockService;

    /**
     * Closed constructor. Please check factory method {@link #create(String, ZonedDateTime, Integer, ILockService)}
     */
    private JLock(String key, ZonedDateTime acquiredAt, Integer timeoutMs, ILockService lockService) {
        this.key = key;
        this.acquiredAt = acquiredAt;
        this.timeoutMs = timeoutMs;
        this.lockService = lockService;
    }

    /**
     * Creates the new lock instance with the given key. Checks that key is not empty.
     *
     * @param key       key of the lock
     * @param dateTime  dateTime of key acquiring
     * @param timeoutMs timeout of this key
     * @return created {@link JLock} instance
     * @throws IllegalArgumentException in case if key is empty
     */
    public static JLock create(String key, ZonedDateTime dateTime, Integer timeoutMs, ILockService lockService) {
        if (StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Key for lock cannot be empty");
        }

        if (lockService == null) {
            throw new IllegalArgumentException("Lock Service must be provided");
        }
        return new JLock(key, dateTime, timeoutMs, lockService);
    }

    /**
     * Releases this lock through the lock service. Can be used in try-with-resources.
     */
    @Override
    public void close() throws Exception {
        lockService.release(this);
    }
}
