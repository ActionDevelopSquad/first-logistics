package com.firstlogistics.companyservice.infrastructure.client;

import com.firstlogistics.companyservice.application.port.HubPort;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubPortAdapter implements HubPort {

    private final HubFeignClient hubFeignClient;

    @Override
    public UUID getHubId(double latitude, double longitude) {
        // TODO: hub-service 준비되면 아래 FeignClient 코드로 교체
        return UUID.fromString("00000000-0000-0000-0000-000000000001");

        // var response = hubFeignClient.findNearestHubId(latitude, longitude);
        // if (response == null || response.getData() == null) {
        //     throw new CompanyException(CompanyErrorCode.INVALID_HUB_ID);
        // }
        // return response.getData();
    }
}
