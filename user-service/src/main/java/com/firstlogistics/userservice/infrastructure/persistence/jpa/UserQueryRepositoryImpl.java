package com.firstlogistics.userservice.infrastructure.persistence.jpa;

import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.domain.dto.UsersSpec;
import com.firstlogistics.userservice.domain.repository.UserQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.firstlogistics.userservice.infrastructure.persistence.jpa.QUserJpaEntity.userJpaEntity;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<UsersSpec> getUsers(UserGetQuery spec, Pageable pageable) {
        BooleanBuilder builder = UserPredicateBuilder.from(spec);

        List<UsersSpec> content = queryFactory
                .select(Projections.constructor(
                        UsersSpec.class,
                        userJpaEntity.username,
                        userJpaEntity.name,
                        userJpaEntity.phone,
                        userJpaEntity.userRole,
                        userJpaEntity.status,
                        userJpaEntity.slackId,
                        userJpaEntity.hubId,
                        userJpaEntity.lastLoginAt
                ))
                .from(userJpaEntity)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch();

        Long total = queryFactory
                .select(userJpaEntity.count())
                .from(userJpaEntity)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {

        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{userJpaEntity.createdAt.desc(), userJpaEntity.updatedAt.desc()};
        }

        return sort.stream()
                .map(order -> {
                    boolean asc = order.isAscending();

                    return switch (order.getProperty()) {
                        case "createdAt" ->
                                asc ? userJpaEntity.createdAt.asc()
                                        : userJpaEntity.createdAt.desc();

                        case "updatedAt" ->
                                asc ? userJpaEntity.updatedAt.asc()
                                        : userJpaEntity.updatedAt.desc();

                        case "username" ->
                                asc ? userJpaEntity.username.asc()
                                        : userJpaEntity.username.desc();

                        case "name" ->
                                asc ? userJpaEntity.name.asc()
                                        : userJpaEntity.name.desc();

                        case "phone" ->
                                asc ? userJpaEntity.phone.asc()
                                        : userJpaEntity.phone.desc();

                        case "lastLoginAt" ->
                                asc ? userJpaEntity.lastLoginAt.asc()
                                        : userJpaEntity.lastLoginAt.desc();

                        default ->
                                userJpaEntity.createdAt.desc();
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }
}
