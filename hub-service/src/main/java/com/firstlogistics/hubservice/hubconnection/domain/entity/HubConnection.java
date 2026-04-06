package com.firstlogistics.hubservice.hubconnection.domain.entity;

import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Distance;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubconnection.domain.vo.Time;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HubConnection {
    private HubConnectionId id;
    private HubId sourceHubId;
    private HubId destinationHubId;
    private Time time;
    private Distance distance;
    private HubConnectionStatus status;

    public static HubConnection create(
            HubId sourceHubId,
            HubId destinationHubId,
            Time time,
            Distance distance
    ){
        validateHubId(sourceHubId);
        validateHubId(destinationHubId);
        validateTime(time);
        validateDistance(distance);
        validateDifferentHub(sourceHubId, destinationHubId);
        HubConnectionStatus status = HubConnectionStatus.ACTIVE;
        return new HubConnection(HubConnectionId.generate(), sourceHubId, destinationHubId, time, distance, status);
    }

    public static HubConnection reconstitute(
            HubConnectionId id,
            HubId sourceHubId,
            HubId destinationHubId,
            Time time,
            Distance distance,
            HubConnectionStatus status
    ){
        validateHubConnectionId(id);
        validateHubId(sourceHubId);
        validateHubId(destinationHubId);
        validateTime(time);
        validateDistance(distance);
        validateStatus(status);
        return new HubConnection(id, sourceHubId, destinationHubId, time, distance, status);
    }

    public void activate() {
        if(this.status == HubConnectionStatus.ACTIVE)
            throw new HubConnectionException(HubConnectionErrorCode.ALREADY_HUB_CONNECTION_ACTIVE);
        this.status = HubConnectionStatus.ACTIVE;
    }

    public void deactivate() {
        if(this.status == HubConnectionStatus.INACTIVE)
            throw new HubConnectionException(HubConnectionErrorCode.ALREADY_HUB_CONNECTION_INACTIVE);
        this.status = HubConnectionStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == HubConnectionStatus.ACTIVE;
    }
    public boolean isInActive() {
        return this.status == HubConnectionStatus.INACTIVE;
    }

    public void changeDistance(Distance distance){
        validateDistance(distance);
        this.distance = distance;
    }

    public void changeTime(Time time){
        validateTime(time);
        this.time = time;
    }

    private static void validateHubConnectionId(HubConnectionId id){
        if(id == null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_ID);
    }
    private static void validateHubId(HubId id){
        if(id == null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_ID);
    }
    private static void validateTime(Time time){
        if(time == null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_MINUTES);
    }
    private static void validateDistance(Distance distance){
        if(distance == null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_METERS);
    }
    private static void validateStatus(HubConnectionStatus status){
        if(status == null)
            throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_STATUS);
    }
    private static void validateDifferentHub(HubId sourceHubId, HubId destinationHubId){
        if(sourceHubId.equals(destinationHubId))
            throw new HubConnectionException(HubConnectionErrorCode.SAME_SOURCE_AND_DESTINATION_HUB);
    }

}
