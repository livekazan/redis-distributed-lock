package com.livekazan.redis.lock;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation to auto configure library
 */
@Target(TYPE)
@Retention(RUNTIME)
@Import(RedisLockConfiguration.class)
public @interface EnableRedisLock {
}
