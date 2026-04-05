package com.firstlogistics.hubservice.hubconnection.infrastructure.feign.adapter;

import com.firstlogistics.hubservice.hubconnection.application.port.RouteMetricPort;
import com.firstlogistics.hubservice.hubconnection.application.port.dto.RouteMetricResponse;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.infrastructure.feign.NaverMapClient;
import com.firstlogistics.hubservice.hubconnection.infrastructure.feign.dto.NaverDirectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NaverRouteMetricAdapter implements RouteMetricPort {

    private final NaverMapClient naverMapClient;

    @Value("${naver.map.client-id}")
    private String clientId;

    @Value("${naver.map.client-secret}")
    private String clientSecret;

    @Override
    public RouteMetricResponse getMetrics(
            double originLatitude,
            double originLongitude,
            double destinationLatitude,
            double destinationLongitude
    ) {
        String start = originLongitude + "," + originLatitude;
        String goal = destinationLongitude + "," + destinationLatitude;

        NaverDirectionResponse response = naverMapClient.getDrivingRoute(
                clientId,
                clientSecret,
                start,
                goal
        );

        if (response == null
                || response.route() == null
                || response.route().traoptimal() == null
                || response.route().traoptimal().isEmpty()
                || response.route().traoptimal().getFirst().summary() == null) {
            throw new HubConnectionException(HubConnectionErrorCode.ROUTE_METRIC_UNAVAILABLE);
        }

        NaverDirectionResponse.Summary summary = response.route().traoptimal().getFirst().summary();

        return new RouteMetricResponse(
                summary.distance(),
                summary.duration() / 60000
        );
    }
}
