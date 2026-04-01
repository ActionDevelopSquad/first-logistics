package com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hub.domain.enums.HubStatus;
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
@Table(
        name = "p_hub",
        uniqueConstraints = {
                @UniqueConstraint(name = HubConstraints.UK_HUB_NAME ,columnNames = "name")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SQLRestriction("deleted_at IS NULL")
public class HubJpaEntity extends BaseAuditEntity {
    @Id
    private UUID id;

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
}
