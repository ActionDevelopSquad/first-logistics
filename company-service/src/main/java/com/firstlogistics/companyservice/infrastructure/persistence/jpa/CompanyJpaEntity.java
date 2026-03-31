package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import com.firstlogistics.companyservice.domain.enums.CompanyStatus;
import common.jpa.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_company")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class CompanyJpaEntity extends BaseAuditEntity {

    @Id
    @Column(columnDefinition = "uuid", nullable = false)
    private UUID id;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID hubId;

    @Column(columnDefinition = "uuid", nullable = false)
    private UUID userId;

    @Column(length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private CompanyStatus status;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(nullable = false, length = 255)
    private String roadAddress;

    @Column(nullable = false, length = 255)
    private String detailAddress;

    private double latitude;

    private double longitude;
}
