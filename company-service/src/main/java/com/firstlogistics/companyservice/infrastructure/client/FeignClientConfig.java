package com.firstlogistics.companyservice.infrastructure.client;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.firstlogistics.companyservice.infrastructure.client")
public class FeignClientConfig {
}
