package com.livekazan.redis.lock.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootConfiguration
@ComponentScan("com.livekazan.redis.lock")
public class TestConfiguration {

    public static final String TYPED_OBJECT_MAPPER_QUALIFIER = "TYPED_OBJECT_MAPPER_QUALIFIER";
    public static final String REDIS_CONNECTION_FACTORY_QUALIFIER = "jedisConnectionFactory";


    @Value("${spring.redis.port}")
    private int redisPort;

    @Autowired
    @Qualifier(REDIS_CONNECTION_FACTORY_QUALIFIER)
    private RedisConnectionFactory redisConnectionFactory;

    @Autowired
    @Qualifier(TYPED_OBJECT_MAPPER_QUALIFIER)
    private ObjectMapper objectMapper;

    @Bean
    @Qualifier(TYPED_OBJECT_MAPPER_QUALIFIER)
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }


    @Bean
    @Qualifier(REDIS_CONNECTION_FACTORY_QUALIFIER)
    public RedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConFactory
            = new JedisConnectionFactory();
        jedisConFactory.setHostName("localhost");
        jedisConFactory.setPort(redisPort);
        return jedisConFactory;
    }

    @Bean
    @Qualifier("redisTemplate")
    public RedisTemplate<String, String> redisTemplate() {
        final RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        return template;
    }

}
