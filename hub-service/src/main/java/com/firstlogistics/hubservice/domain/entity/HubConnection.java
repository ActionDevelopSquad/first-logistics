package com.firstlogistics.hubservice.domain.entity;

import com.firstlogistics.hubservice.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.domain.vo.Distance;
import com.firstlogistics.hubservice.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.domain.vo.HubId;
import com.firstlogistics.hubservice.domain.vo.Time;
import lombok.AllArgsConstructor;

@AllArgsConstructor
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
        //검증 메서드
        HubConnectionStatus status = HubConnectionStatus.ACTIVE;
        return new HubConnection(HubConnectionId.generate(), sourceHubId, destinationHubId, time, distance, status);
    }

    public static HubConnection reconstruct(
            HubConnectionId id,
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
