package com.firstlogistics.hubservice.domain.entity;

import com.firstlogistics.hubservice.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.domain.enums.HubStatus;
import com.firstlogistics.hubservice.domain.vo.Distance;
import com.firstlogistics.hubservice.domain.vo.HubId;
import com.firstlogistics.hubservice.domain.vo.Time;

import java.util.UUID;

public class HubConnection {
    private UUID id;
    private HubId sourceHubId;
    private HubId destinationHubId;
    private Time time;
    private Distance distance;
    private HubConnectionStatus status;

    private HubConnection(
            UUID id,
            HubId sourceHubId,
            HubId destinationHubId,
            Time time,
            Distance distance,
            HubConnectionStatus status) {
        //private 검증 메서드
        this.id = id;
        this.destinationHubId = destinationHubId;
        this.sourceHubId = sourceHubId;
        this.time = time;
        this.distance = distance;
        this.status = status;
    }
    public static HubConnection create(
            HubId sourceHubId,
            HubId destinationHubId,
            Time time,
            Distance distance
    ){
        UUID id = UUID.randomUUID();
        HubConnectionStatus status = HubConnectionStatus.ACTIVE;
        return new HubConnection(id, sourceHubId, destinationHubId, time, distance, status);
    }

    public static HubConnection reconstruct(
            UUID id,
            HubId sourceHubId,
            HubId destinationHubId,
            Time time,
            Distance distance,
            HubConnectionStatus status
    ){
        return new HubConnection(id, sourceHubId, destinationHubId, time, distance, status);
    }

    public void activate() {
        this.status = HubConnectionStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = HubConnectionStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == HubConnectionStatus.ACTIVE;
    }
}
