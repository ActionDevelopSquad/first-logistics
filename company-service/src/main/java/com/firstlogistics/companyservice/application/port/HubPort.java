package com.firstlogistics.companyservice.application.port;

import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import java.util.UUID;

public interface HubPort {

    UUID getHubId(GeoLocation geoLocation);
}
