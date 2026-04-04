package com.firstlogistics.hubservice.hubconnection.application.port;

import com.firstlogistics.hubservice.hubconnection.domain.enums.RoutePolicy;

public interface RoutePolicyProvider {
    RoutePolicy getDefaultPolicy();
}
