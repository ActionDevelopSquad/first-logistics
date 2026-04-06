package com.firstlogistics.hubservice.hubconnection.infrastructure.config;

import com.firstlogistics.hubservice.hubconnection.application.port.RoutePolicyProvider;
import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PropertiesRoutePolicyProvider implements RoutePolicyProvider {
    private final RouteProperties routeProperties;

    @Override
    public RoutePolicy getDefaultPolicy() {
        return routeProperties.getDefaultPolicy();
    }
}
