package com.firstlogistics.companyservice.infrastructure.client;

import common.security.config.FeignAuthPropagationConfig;
import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.firstlogistics.companyservice.infrastructure.client", defaultConfiguration = FeignAuthPropagationConfig.class)
public class FeignConfig {

    private static final String SERVICE_CODE_HEADER = "X-Forward-Service-Code";
    private static final String SERVICE_CODE = "company-service";

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> template.header(SERVICE_CODE_HEADER, SERVICE_CODE);
    }
}
