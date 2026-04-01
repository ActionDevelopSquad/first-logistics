package com.firstlogistics.deliverservice.infrastructure.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(
            @Value("${spring.data.redis.url}") String redisUrl
    ) {
        Config config = new Config();
        config.useSingleServer().setAddress(redisUrl);
        return Redisson.create(config);
    }
}