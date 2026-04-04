package com.firstlogistics.hubservice.hubconnection.infrastructure.config;

import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "route")
@Component
public class RouteProperties {
    private RoutePolicy defaultPolicy;
}
