package com.firstlogistics.hubservice.hubconnection.infrastructure.config;

import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "route")
@Component
public class RouteProperties {
    @NotNull
    private RoutePolicy defaultPolicy;
}
