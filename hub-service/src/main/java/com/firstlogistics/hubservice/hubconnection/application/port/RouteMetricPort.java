package com.firstlogistics.hubservice.hubconnection.application.port;

import com.firstlogistics.hubservice.hubconnection.application.port.dto.RouteMetricResponse;

public interface RouteMetricPort {
    RouteMetricResponse getMetrics(
            double originLatitude,
            double originLongitude,
            double destinationLatitude,
            double destinationLongitude
    );
}
