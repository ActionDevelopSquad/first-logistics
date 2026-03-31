package com.firstlogistics.userservice.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByIdAndDeletedAtIsNull(UUID userId);

    Optional<UserJpaEntity> findByUsernameAndDeletedAtIsNull(String username);
}
