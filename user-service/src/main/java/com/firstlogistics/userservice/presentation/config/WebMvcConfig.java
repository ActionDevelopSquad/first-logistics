package com.firstlogistics.userservice.presentation.config;

import com.firstlogistics.userservice.presentation.pageable.CustomPageRequest;
import com.firstlogistics.userservice.presentation.pageable.CustomPageableResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customPageableResolver());
    }

    private CustomPageableResolver customPageableResolver() {
        CustomPageableResolver resolver = new CustomPageableResolver();
        resolver.setFallbackPageable(CustomPageRequest.defaultPageable());
        resolver.setOneIndexedParameters(false);
        return resolver;
    }
}