package com.firstlogistics.hubservice.hubconnection.application.dto.query;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.specification.HubConnectionSpec;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;

import java.util.UUID;

public record SearchHubConnectionQuery(
        UUID sourceHubId,
        UUID destinationHubId,
        Integer time,
        Integer distance,
        String status
) {
    public HubConnectionSpec toSpec(){
        if (sourceHubId != null && sourceHubId.equals(destinationHubId))
            throw new HubConnectionException(HubConnectionErrorCode.SAME_SOURCE_AND_DESTINATION_HUB);

        return new HubConnectionSpec(
                sourceHubId == null ? null : HubId.of(sourceHubId),
                destinationHubId == null ? null : HubId.of(destinationHubId),
                time == null ? null : Time.of(time),
                distance == null ? null : Distance.of(distance),
                parseStatus(status)
        );
    }

    private HubConnectionStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return HubConnectionStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_STATUS);
        }
    }

}
