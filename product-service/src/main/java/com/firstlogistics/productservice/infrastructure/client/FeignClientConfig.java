package com.firstlogistics.productservice.infrastructure.client;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.firstlogistics.productservice.infrastructure.client")
public class FeignClientConfig {

    private static final String SERVICE_CODE_HEADER = "X-Forward-Service-Code";
    private static final String SERVICE_CODE = "product-service";

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return template -> template.header(SERVICE_CODE_HEADER, SERVICE_CODE);
    }
}
