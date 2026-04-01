package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa;

import com.firstlogistics.hubservice.hubconnection.domain.enums.HubConnectionStatus;
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
        name = "p_hub_connection",
        uniqueConstraints = {
            @UniqueConstraint(name = "uk_hub_connection_hub_id", columnNames = {"source_hub_id", "destination_hub_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SQLRestriction("deleted_at IS NULL")
public class HubConnectionJpaEntity extends BaseAuditEntity {
    @Id
    private UUID id;

    @Column(name = "source_hub_id", nullable = false)
    private UUID sourceHubId;

    @Column(name = "destination_hub_id", nullable = false)
    private UUID destinationHubId;

    @Column(nullable = false)
    private int minutes;

    @Column(nullable = false)
    private int meters;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HubConnectionStatus status;
}
