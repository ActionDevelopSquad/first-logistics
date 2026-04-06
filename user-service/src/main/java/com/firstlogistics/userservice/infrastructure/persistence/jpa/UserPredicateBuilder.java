package com.firstlogistics.userservice.infrastructure.persistence.jpa;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.domain.enums.Status;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import common.security.entity.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.firstlogistics.userservice.infrastructure.persistence.jpa.QUserJpaEntity.userJpaEntity;

public class UserPredicateBuilder {

    private UserPredicateBuilder() {
    }

    public static BooleanBuilder from(UserGetQuery spec) {
        BooleanBuilder builder = new BooleanBuilder();

        if (hasText(spec.username())) {
            builder.and(usernameContains(spec.username()));
        }

        if (hasText(spec.name())) {
            builder.and(nameContains(spec.name()));
        }

        if (hasText(spec.phone())) {
            builder.and(phoneContains(spec.phone()));
        }

        if (spec.userRole() != null) {
            builder.and(roleEq(spec.userRole()));
        }

        if (spec.status() != null) {
            builder.and(statusEq(spec.status()));
        }

        if (hasText(spec.slackId())) {
            builder.and(slackIdContains(spec.slackId()));
        }

        if (spec.hubId() != null) {
            builder.and(hubIdEq(spec.hubId()));
        }

        if (spec.lastLoginAt() != null) {
            builder.and(lastLoginAfter(spec.lastLoginAt()));
        }

        return builder;
    }

    private static BooleanExpression usernameContains(String username) {
        return userJpaEntity.username.containsIgnoreCase(username);
    }

    private static BooleanExpression nameContains(String name) {
        return userJpaEntity.name.containsIgnoreCase(name);
    }

    private static BooleanExpression phoneContains(String phone) {
        return userJpaEntity.phone.contains(phone);
    }

    private static BooleanExpression roleEq(UserRole role) {
        return userJpaEntity.userRole.eq(role);
    }

    private static BooleanExpression statusEq(Status status) {
        return userJpaEntity.status.eq(status);
    }

    private static BooleanExpression slackIdContains(String slackId) {
        return userJpaEntity.slackId.containsIgnoreCase(slackId);
    }

    private static BooleanExpression hubIdEq(UUID hubId) {
        return userJpaEntity.hubId.eq(hubId);
    }

    private static BooleanExpression lastLoginAfter(LocalDateTime lastLoginAt) {
        return userJpaEntity.lastLoginAt.goe(lastLoginAt);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}