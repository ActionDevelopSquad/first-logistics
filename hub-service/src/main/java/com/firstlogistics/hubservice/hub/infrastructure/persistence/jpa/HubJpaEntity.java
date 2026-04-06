package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
import com.firstlogistics.hubservice.hub.domain.enums.HubType;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@Getter
@Entity
@Table(name = "p_hub")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SQLRestriction("deleted_at IS NULL")
public class HubJpaEntity extends BaseAuditEntity {
    @Id
    private UUID id;
    @Version
    @Column(nullable = false)
    private Long version;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(name = "road_address", nullable = false)
    private String roadAddress;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HubStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HubType type;

    public void changeName(String name) {
        this.name = name;
    }

    public void changeAddress(String roadAddress) {
        this.roadAddress = roadAddress;
    }

    public void changeLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void changeType(HubType type) {
        this.type = type;
    }

    public void changeStatus(HubStatus status) {
        this.status = status;
    }
}
