package com.firstlogistics.hubservice.global.config.cache;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {
    private static final String HUB_BY_ID_CACHE = "hub:byId";
    private static final String HUB_ALL_CACHE = "hub:all";
    private static final String HUB_CONNECTION_ALL_CACHE = "hubConnection:all";
    private static final String ROUTE_HYBRID_RESULT_CACHE = "route:hybrid:result";
    private static final String ROUTE_HUB_TO_HUB_RESULT_CACHE = "route:hubTohub:result";

    private static final Duration DEFAULT_TTL = Duration.ofDays(1);
    private static final Duration TTL_30_DAYS = Duration.ofDays(30);
    private static final Duration TTL_10_DAYS = Duration.ofDays(10);
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.activateDefaultTyping(
                BasicPolymorphicTypeValidator.builder()
                        .allowIfSubType("com.firstlogistics.hubservice")
                        .allowIfSubType("java.util")
                        .build(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
                )
                .entryTtl(DEFAULT_TTL)
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(HUB_BY_ID_CACHE, defaultConfig.entryTtl(TTL_30_DAYS));
        cacheConfigurations.put(HUB_ALL_CACHE, defaultConfig.entryTtl(TTL_30_DAYS));
        cacheConfigurations.put(HUB_CONNECTION_ALL_CACHE, defaultConfig.entryTtl(TTL_30_DAYS));
        cacheConfigurations.put(ROUTE_HYBRID_RESULT_CACHE, defaultConfig.entryTtl(TTL_10_DAYS));
        cacheConfigurations.put(ROUTE_HUB_TO_HUB_RESULT_CACHE, defaultConfig.entryTtl(TTL_10_DAYS));


        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
