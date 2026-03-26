package com.firstlogistics.hubservice.domain.entity;

import com.firstlogistics.hubservice.domain.enums.HubConnectionStatus;
import com.firstlogistics.hubservice.domain.enums.HubStatus;
import com.firstlogistics.hubservice.domain.vo.*;

import java.util.UUID;


public class Hub {
    private HubId id;
    private String name;
    private HubAddress address;
    private GeoLocation geoLocation;
    private HubStatus status;

    private Hub(
            HubId id,
            String name,
            HubAddress address,
            GeoLocation geoLocation,
            HubStatus status) {
        //private 검증 메서드
        this.id = id;
        this.name = name;
        this.address = address;
        this.geoLocation = geoLocation;
        this.status = status;
    }

    public static Hub create(
            String name,
            HubAddress address,
            GeoLocation geoLocation
    ) {
        HubStatus status = HubStatus.ACTIVE;
        return new Hub(HubId.generate(), name, address, geoLocation, status);
    }

    public static Hub reconstruct(
            HubId id,
            String name,
            HubAddress address,
            GeoLocation geoLocation,
            HubStatus status)
    {
        return new Hub(id, name, address, geoLocation, status);
    }

    public void activate() {
        this.status = HubStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = HubStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == HubStatus.ACTIVE;
    }
    public void changeAddress(HubAddress address){
        this.address = address;
    }
    public void changeName(String name){
        this.name = name;
    }
}
