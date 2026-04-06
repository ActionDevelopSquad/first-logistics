package com.firstlogistics.hubservice.hubconnection.domain.specification;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;

public record HubConnectionSpec(
        HubId sourceHubId,
        HubId destinationHubId,
        Time time,
        Distance distance,
        HubConnectionStatus status
) {
}
