package com.firstlogistics.companyservice.application.port;

import java.util.UUID;

public interface HubPort {

    UUID getHubId(double latitude, double longitude);
}
