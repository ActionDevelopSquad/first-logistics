package com.firstlogistics.hubservice.hubconnection.infrastructure.external;

import com.firstlogistics.hubservice.hubconnection.infrastructure.external.dto.NaverDirectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "naver-map-client",
        url = "${naver.map.url}"
)
public interface NaverMapClient {

    @GetMapping("/map-direction/v1/driving")
    NaverDirectionResponse getDrivingRoute(
            @RequestHeader("x-ncp-apigw-api-key-id") String clientId,
            @RequestHeader("x-ncp-apigw-api-key") String clientSecret,
            @RequestParam("start") String start,
            @RequestParam("goal") String goal
    );
}
