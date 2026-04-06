package com.firstlogistics.orderservice.infrastructure.feign.config;

import common.security.config.FeignAuthPropagationConfig;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.firstlogistics.orderservice.infrastructure.feign", defaultConfiguration = FeignAuthPropagationConfig.class)
public class FeignConfig {
}
