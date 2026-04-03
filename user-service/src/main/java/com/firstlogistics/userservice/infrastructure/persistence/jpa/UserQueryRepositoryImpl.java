package com.firstlogistics.userservice.infrastructure.persistence.jpa;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.domain.dto.UsersSpec;
import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.repository.UserQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import common.jpa.entity.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.firstlogistics.userservice.infrastructure.persistence.jpa.QUserJpaEntity.userJpaEntity;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UsersSpec> getUsers(UserGetQuery spec, Pageable pageable) {
        List<UsersSpec> content = queryFactory
                .select(Projections.constructor(
                        UsersSpec.class,
                        userJpaEntity.username,
                        userJpaEntity.name,
                        userJpaEntity.phone,
                        userJpaEntity.userRole,
                        userJpaEntity.status,
                        userJpaEntity.slackId,
                        userJpaEntity.organizationId,
                        userJpaEntity.lastLoginAt
                ))
                .from(userJpaEntity)
                .where(
                        usernameContains(spec.username()),
                        nameContains(spec.name()),
                        phoneContains(spec.phone()),
                        roleEq(spec.userRole()),
                        statusEq(spec.status()),
                        slackIdContains(spec.slackId()),
                        organizationEq(spec.organizationId()),
                        lastLoginAfter(spec.lastLoginAt())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(userJpaEntity.createdAt.desc())
                .fetch();

        Long total = queryFactory
                .select(userJpaEntity.count())
                .from(userJpaEntity)
                .where(
                        usernameContains(spec.username()),
                        nameContains(spec.name()),
                        phoneContains(spec.phone()),
                        roleEq(spec.userRole()),
                        statusEq(spec.status()),
                        slackIdContains(spec.slackId()),
                        organizationEq(spec.organizationId()),
                        lastLoginAfter(spec.lastLoginAt()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression usernameContains(String username) {
        return username != null ? userJpaEntity.username.containsIgnoreCase(username) : null;
    }

    private BooleanExpression nameContains(String name) {
        return name != null ? userJpaEntity.name.containsIgnoreCase(name) : null;
    }

    private BooleanExpression phoneContains(String phone) {
        return phone != null ? userJpaEntity.phone.contains(phone) : null;
    }

    private BooleanExpression roleEq(UserRole role) {
        return role != null ? userJpaEntity.userRole.eq(role) : null;
    }

    private BooleanExpression statusEq(Status status) {
        return status != null ? userJpaEntity.status.eq(status) : null;
    }

    private BooleanExpression slackIdContains(String slackId) {
        return slackId != null ? userJpaEntity.slackId.contains(slackId) : null;
    }

    private BooleanExpression organizationEq(UUID organizationId) {
        return organizationId != null ? userJpaEntity.organizationId.eq(organizationId) : null;
    }

    private BooleanExpression lastLoginAfter(LocalDateTime lastLoginAt) {
        return lastLoginAt != null ? userJpaEntity.lastLoginAt.goe(lastLoginAt) : null;
    }
}
