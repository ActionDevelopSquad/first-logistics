package com.firstlogistics.userservice.infrastructure.persistence.jpa;

import com.firstlogistics.userservice.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * JPA Entity -> Domain
     */
    public User toDomain(UserJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.reconstitute(
                entity.getId(),
                entity.getUsername(),
                entity.getName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getSlackId(),
                entity.getStatus(),
                entity.getUserRole(),
                entity.getOrganizationId(),
                entity.getLastLoginAt()
        );
    }

    /**
     * Domain -> JPA Entity
     */
    public UserJpaEntity toEntity(User domain) {
        if (domain == null) {
            return null;
        }

        return new UserJpaEntity(
                domain.getId(),
                domain.getUsername(),
                domain.getName(),
                domain.getPhone(),
                domain.getEmail(),
                domain.getSlackId(),
                domain.getStatus(),
                domain.getUserRole(),
                domain.getOrganizationId(),
                domain.getLastLoginAt()
        );
    }
}
