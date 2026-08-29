package com.projectone.distributedjobprocessingplatform.config;

import com.projectone.distributedjobprocessingplatform.dto.JobResponse;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory) {

        JsonMapper jsonMapper = JsonMapper.builder().build();

        JacksonJsonRedisSerializer<JobResponse> serializer =
                new JacksonJsonRedisSerializer<>(jsonMapper, JobResponse.class);

        RedisCacheConfiguration cacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(serializer)
                        );

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfiguration)
                .build();
    }
}