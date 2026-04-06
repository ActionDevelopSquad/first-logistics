package com.firstlogistics.companyservice.infrastructure.client;

import com.firstlogistics.companyservice.application.port.HubPort;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import feign.FeignException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HubPortAdapter implements HubPort {

    private final HubFeignClient hubFeignClient;

    @Override
    public UUID getHubId(GeoLocation geoLocation) {
        try {
            var response = hubFeignClient.findNearestHubId(geoLocation.latitude(), geoLocation.longitude());
            if (response == null || response.data() == null) {
                throw new CompanyException(CompanyErrorCode.INVALID_HUB_ID);
            }
            return response.data().hubId();
        } catch (FeignException.BadRequest e) {
            throw new CompanyException(CompanyErrorCode.HUB_LOCATION_OUT_OF_RANGE);
        }
    }
}
