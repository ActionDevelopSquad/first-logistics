package com.firstlogistics.hubservice.hubconnection.infrastructure.config;

import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "route")
public class RouteProperties {
    private RoutePolicy defaultPolicy;
}
