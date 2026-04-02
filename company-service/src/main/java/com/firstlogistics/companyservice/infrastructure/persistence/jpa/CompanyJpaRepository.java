package com.firstlogistics.companyservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CompanyJpaRepository extends JpaRepository<CompanyJpaEntity, UUID> {

    Optional<CompanyJpaEntity> findByIdAndDeletedAtIsNull(UUID id);

    Optional<CompanyJpaEntity> findByManagerIdAndDeletedAtIsNull(UUID managerId);

    boolean existsByManagerIdAndDeletedAtIsNull(UUID managerId);
}
