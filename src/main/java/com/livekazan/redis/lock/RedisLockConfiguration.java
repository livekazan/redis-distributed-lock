package com.livekazan.redis.lock;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.livekazan.redis.lock"})
public class RedisLockConfiguration {
}
