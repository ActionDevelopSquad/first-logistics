package com.firstlogistics.companyservice.domain.entity;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import com.firstlogistics.companyservice.domain.exception.CompanyErrorCode;
import com.firstlogistics.companyservice.domain.exception.CompanyException;
import com.firstlogistics.companyservice.domain.vo.CompanyAddress;
import com.firstlogistics.companyservice.domain.vo.GeoLocation;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Getter
public class Company {

    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;
    private UUID hubId;
    private UUID managerId;
    private String name;
    private CompanyType type;
    private CompanyStatus status;
    private CompanyAddress address;
    private GeoLocation geoLocation;

    public static Company create(UUID hubId, UUID managerId, String name, CompanyType type, String roadAddress,
                                 String detailAddress, double latitude, double longitude) {
        validate(hubId, managerId, name, type);

        return new Company(
                UUID.randomUUID(),
                hubId,
                managerId,
                name,
                type,
                CompanyStatus.ACTIVE,
                CompanyAddress.of(roadAddress, detailAddress),
                GeoLocation.of(latitude, longitude)
        );
    }

    private static void validate(UUID hubId, UUID managerId, String name, CompanyType type) {
        if (hubId == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_HUB_ID);
        }
        if (managerId == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_MANAGER_ID);
        }
        if (name == null || name.isBlank()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_NAME);
        }
        if (type == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_TYPE);
        }
    }

    public static Company reconstitute(UUID id, UUID hubId, UUID managerId, String name, CompanyType companyType,
                                       CompanyStatus status, CompanyAddress address, GeoLocation geoLocation) {
        return new Company(
                id,
                hubId,
                managerId,
                name,
                companyType,
                status,
                address,
                geoLocation
        );
    }

    public void update(String name, CompanyType type, UUID hubId,
                       String roadAddress, String detailAddress, double latitude, double longitude) {
        if (this.status == CompanyStatus.INACTIVE) {
            throw new CompanyException(CompanyErrorCode.COMPANY_INACTIVE);
        }
        if (name == null || name.isBlank()) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_NAME);
        }
        if (type == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_COMPANY_TYPE);
        }
        if (hubId == null) {
            throw new CompanyException(CompanyErrorCode.INVALID_HUB_ID);
        }

        this.name = name;
        this.type = type;
        this.hubId = hubId;
        this.address = CompanyAddress.of(roadAddress, detailAddress);
        this.geoLocation = GeoLocation.of(latitude, longitude);
    }

    public void changeAddress(String roadAddress, String detailAddress, double latitude, double longitude) {
        if (this.status == CompanyStatus.INACTIVE) {
            throw new CompanyException(CompanyErrorCode.COMPANY_INACTIVE);
        }

        this.address = CompanyAddress.of(roadAddress, detailAddress);
        this.geoLocation = GeoLocation.of(latitude, longitude);
    }

    public void deactivate() {
        if (this.status == CompanyStatus.INACTIVE) {
            throw new CompanyException(CompanyErrorCode.COMPANY_ALREADY_INACTIVE);
        }
        this.status = CompanyStatus.INACTIVE;
    }

    public void activate() {
        if (this.status == CompanyStatus.ACTIVE) {
            throw new CompanyException(CompanyErrorCode.COMPANY_ALREADY_ACTIVE);
        }
        this.status = CompanyStatus.ACTIVE;
    }

    public boolean canRegisterProduct() {
        return type.canRegisterProduct();
    }

    public boolean canHoldInventory() {
        return type.canHoldInventory();
    }
}
