package com.firstlogistics.hubservice.global.config.feign;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.firstlogistics.hubservice")
@ConditionalOnProperty(name = "feign.enabled", havingValue = "true", matchIfMissing = true)
public class FeignClientConfig {
}
