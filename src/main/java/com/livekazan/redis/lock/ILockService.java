package com.livekazan.redis.lock;


import com.livekazan.redis.lock.exception.LockingException;
import com.livekazan.redis.lock.exception.UnlockingException;

/**
 * Interface to manage locks for redis.
 */
public interface ILockService {

    /**
     * Acquire the distributed lock on the given prefix and id in redis.
     * The acquiring will be tried once and returns immediately. If lock is not successful the exception is thrown
     * The key for the lock will be in form &quot;account:prefix:id&quot;.
     *
     * @param key key_type:id
     * @return {@link IJLock} instance
     * @throws IllegalArgumentException is case if prefix or id is empty
     * @throws LockingException         in case of error on acquiring the lock
     */
    IJLock acquire(String redisPrefix, String key, Integer releaseTimeMs) throws LockingException;

    /**
     * Releases the given lock. If the lock is expired the exception is thrown.
     *
     * @param lock lock to be released
     * @throws UnlockingException in case of error on releasing the lock
     */
    void release(IJLock lock) throws UnlockingException;

    /**
     * Marks given lock as failed after release.
     *
     * @param lock the lock
     */

    void markAsFailed(IJLock lock);

}
