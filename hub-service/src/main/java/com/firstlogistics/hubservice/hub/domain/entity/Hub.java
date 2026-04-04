package com.firstlogistics.hubservice.hub.domain.entity;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.vo.GeoLocation;
import com.firstlogistics.hubservice.hub.domain.vo.HubAddress;
import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hub {
    private static final int MAX_NAME_LENGTH = 30;

    private HubId id;
    private String name;
    private HubAddress address;
    private GeoLocation geoLocation;
    private HubStatus status;
    private HubType type;


    public static Hub create(
            String name,
            HubAddress address,
            GeoLocation geoLocation,
            HubType hubType
    ) {
        validateName(name);
        validateAddress(address);
        validateGeoLocation(geoLocation);

        return new Hub(HubId.generate(), name, address, geoLocation, HubStatus.ACTIVE,hubType);
    }

    public static Hub reconstitute(
            HubId id,
            String name,
            HubAddress address,
            GeoLocation geoLocation,
            HubStatus status,
            HubType hubType
    ) {
        validateType(hubType);
        validateId(id);
        validateStatus(status);
        validateName(name);
        validateAddress(address);
        validateGeoLocation(geoLocation);

        return new Hub(id, name, address, geoLocation, status, hubType);
    }

    public void activate() {
        if(this.status == HubStatus.ACTIVE)
            throw new HubException(HubErrorCode.ALREADY_HUB_ACTIVE);
        this.status = HubStatus.ACTIVE;
    }

    public void deactivate() {
        if(this.status == HubStatus.INACTIVE)
            throw new HubException(HubErrorCode.ALREADY_HUB_INACTIVE);
        this.status = HubStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == HubStatus.ACTIVE;
    }

    public void changeAddress(HubAddress address) {
        validateAddress(address);
        this.address = address;
    }

    public void changeName(String name) {
        validateName(name);
        this.name = name;
    }

    private static void validateId(HubId id) {
        if (id == null) {
            throw new HubException(HubErrorCode.INVALID_HUB_ID);
        }
    }

    private static void validateStatus(HubStatus status) {
        if (status == null) {
            throw new HubException(HubErrorCode.INVALID_HUB_STATUS);
        }
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new HubException(HubErrorCode.INVALID_HUB_NAME);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new HubException(HubErrorCode.INVALID_HUB_NAME);
        }
    }

    private static void validateAddress(HubAddress address) {
        if (address == null) {
            throw new HubException(HubErrorCode.INVALID_HUB_ADDRESS);
        }
    }

    private static void validateGeoLocation(GeoLocation geoLocation) {
        if (geoLocation == null) {
            throw new HubException(HubErrorCode.INVALID_HUB_GEOLOCATION);
        }
    }
    private static void validateType(HubType hubType){
        if(hubType == null)
            throw new HubException(HubErrorCode.INVALID_HUB_TYPE);
    }
}
