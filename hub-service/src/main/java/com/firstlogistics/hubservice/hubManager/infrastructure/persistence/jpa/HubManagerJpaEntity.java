package com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa;

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
        name = "p_hub_manager",
        uniqueConstraints = {
                @UniqueConstraint(name = HubManagerConstraints.UK_HUB_MANAGER_USER_HUB ,columnNames = {"userId","hubId"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@SQLRestriction("deleted_at IS NULL")
public class HubManagerJpaEntity extends BaseAuditEntity  {
    @Id
    private UUID id;

    private UUID userId;

    private UUID hubId;
}