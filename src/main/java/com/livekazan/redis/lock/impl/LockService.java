package com.livekazan.redis.lock.impl;


import com.livekazan.redis.lock.IJLock;
import com.livekazan.redis.lock.ILockService;
import com.livekazan.redis.lock.exception.LockingException;
import com.livekazan.redis.lock.exception.UnlockingException;
import com.livekazan.redis.lock.util.IRedisDateUtils;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static java.time.ZonedDateTime.now;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Implementation of the {@link ILockService} interface.
 */
@Slf4j
@Service
public class LockService implements ILockService {

    private static final String LOCK_VALUE = "LOCKED";
    private static final String LOCK_IS_ALREADY_ACQUIRED = "Lock is already acquired";
    private static final String REDIS_TEMPLATE_IS_NULL = "RedisTemplate must be provided. Check redis connection.";
    private static final String LOCK_IS_MISSED = "Lock is missed in redis before expire time";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private IRedisDateUtils dateUtils;

    /**
     * {@inheritDoc}
     */
    @Override
    public IJLock acquire(String redisPrefix, String key, Integer releaseTimeMs) throws LockingException {
        try {
            if (redisTemplate == null) {
                throw new IllegalArgumentException(REDIS_TEMPLATE_IS_NULL);
            }
            final ILockService thisService = this;
            return redisTemplate.execute(new SessionCallback<>() {
                @Override
                public IJLock execute(RedisOperations operations) throws DataAccessException {
                    IJLock lock = JLock.create(createKey(redisPrefix, key), dateUtils.getZonedDateTime(),
                        releaseTimeMs, thisService);
                    //start watching for key. Read in transaction
                    operations.watch(lock.getKey());
                    if (operations.opsForValue().get(lock.getKey()) != null) {
                        throw new CannotAcquireLockException(LOCK_IS_ALREADY_ACQUIRED);
                    }
                    //write in transaction
                    operations.multi();
                    operations.opsForValue().setIfAbsent(lock.getKey(), LOCK_VALUE, releaseTimeMs, MILLISECONDS);
                    List execResult = operations.exec();
                    //rollback check. Jedis returns, in any case, a List object. Other implementations can return null
                    // Here result returns the result of each operation in the transaction, and if the setIfAbsent
                    // operation fails, result [0] will be false.
                    if (execResult == null || execResult.isEmpty() || Boolean.FALSE.equals(execResult.get(0))) {
                        throw new CannotAcquireLockException(LOCK_IS_ALREADY_ACQUIRED);
                    }
                    return lock;
                }
            });
        } catch (CannotAcquireLockException e) {
            log.info("Cannot acquire the lock because it is already acquired. key: {}", key);
            throw new LockingException(LOCK_IS_ALREADY_ACQUIRED, e);
        } catch (Exception e) {
            log.error("Error on lock acquiring for key: {}", key, e);
            throw new LockingException("Cannot acquire the lock. Unexpected error", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void release(IJLock lock) throws UnlockingException {
        try {
            if (redisTemplate == null) {
                throw new IllegalArgumentException(REDIS_TEMPLATE_IS_NULL);
            }
            redisTemplate.execute(new SessionCallback<IJLock>() {
                @Override
                public IJLock execute(RedisOperations operations) throws DataAccessException {
                    //start watching for key. Read in transaction
                    operations.watch(lock.getKey());
                    if (operations.opsForValue().get(lock.getKey()) == null) {
                        long difference = Math.abs(dateUtils.getDifference(lock.getAcquiredAt(), now(), MILLIS));
                        if (difference > lock.getTimeoutMs()) {
                            log.error("Lock is expired! Look time {} ms. Check queue of events! Lock {}", difference,
                                lock);
                            return lock;
                        } else {
                            throw new EmptyResultDataAccessException(LOCK_IS_MISSED, 1);
                        }
                    }
                    //transaction start
                    operations.multi();
                    operations.delete(lock.getKey());
                    operations.exec();
                    return lock;
                }
            });
            log.info("Released lock: {}", lock);
        } catch (Exception e) {
            throw new UnlockingException("Lock release error", e);
        }
    }

    @Override
    public void markAsFailed(IJLock lock) {
        log.error("Error during lock release: {}", lock);
    }

    private String createKey(String redisPrefix, String key) {
        if (key == null) {
            throw new IllegalArgumentException("Redis lock key must not be empty. key must be provided");
        }
        return redisPrefix + StringUtils.trimAllWhitespace(key);
    }
}
