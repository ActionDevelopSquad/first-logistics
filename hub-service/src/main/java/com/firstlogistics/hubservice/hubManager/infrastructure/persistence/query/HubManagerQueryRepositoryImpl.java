package com.firstlogistics.hubservice.hubManager.infrastructure.persistence.query;

import com.firstlogistics.hubservice.hub.domain.vo.HubId;
import com.firstlogistics.hubservice.hubManager.domain.entity.HubManager;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerErrorCode;
import com.firstlogistics.hubservice.hubManager.domain.exception.HubManagerException;
import com.firstlogistics.hubservice.hubManager.domain.repository.HubManagerQueryRepository;
import com.firstlogistics.hubservice.hubManager.domain.specification.HubManagerSearchSpec;
import com.firstlogistics.hubservice.hubManager.domain.vo.HubManagerId;
import com.firstlogistics.hubservice.hubManager.domain.vo.UserId;
import com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa.HubManagerJpaEntity;
import com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa.HubManagerMapper;
import com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa.QHubManagerJpaEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import static com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa.QHubManagerJpaEntity.hubManagerJpaEntity;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Repository
public class HubManagerQueryRepositoryImpl implements HubManagerQueryRepository {
    private static final QHubManagerJpaEntity hubManager = hubManagerJpaEntity;
    private final JPAQueryFactory queryFactory;

    @Override
    public HubManager findById(HubManagerId hubManagerId) {
        HubManagerJpaEntity result = queryFactory
                .select(hubManager)
                .from(hubManager)
                .where(
                        hubManager.id.eq(hubManagerId.id()),
                        notDeleted()
                )
                .fetchOne();

        if (result == null)
            throw new HubManagerException(HubManagerErrorCode.HUB_MANAGER_NOT_FOUND);

        return HubManagerMapper.toDomain(result);
    }

    @Override
    public Page<HubManager> searchByCondition(HubManagerSearchSpec spec, Pageable pageable) {
        List<HubManager> content = queryFactory
                .selectFrom(hubManager)
                .where(
                        userIdsIn(spec.userIds()),
                        hubIdsIn(spec.hubIds()),
                        notDeleted()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .fetch()
                .stream()
                .map(HubManagerMapper::toDomain)
                .toList();
        Long total = queryFactory
                .select(hubManager.count())
                .from(hubManager)
                .where(
                        userIdsIn(spec.userIds()),
                        hubIdsIn(spec.hubIds()),
                        notDeleted()
                )
                .fetchOne();

        return new PageImpl<>(content,pageable,total == null ? 0 : total);
    }

    @Override
    public HubId findHubIdByUserId(UserId userId) {
        UUID result =  queryFactory
                .select(hubManager.hubId)
                .from(hubManager)
                .where(
                        hubManager.userId.eq(userId.id()),
                        notDeleted()
                )
                .fetchOne();
        if(result == null)
            throw new HubManagerException(HubManagerErrorCode.HUB_MANAGER_NOT_FOUND);

        return HubId.of(result);
    }

    private BooleanExpression notDeleted(){
        return hubManager.deletedAt.isNull();
    }

    private BooleanExpression userIdsIn(List<UUID> userIds){
        return userIds== null || userIds.isEmpty() ? null : hubManager.userId.in(userIds);
    }

    private BooleanExpression hubIdsIn(List<UUID> hubIds){
        return hubIds== null || hubIds.isEmpty() ? null : hubManager.hubId.in(hubIds);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier[]{hubManager.createdAt.desc()};
        }

        return sort.stream()
                .map(order -> {
                    boolean asc = order.isAscending();

                    return switch (order.getProperty()) {
                        case "createdAt" -> asc ? hubManager.createdAt.asc() : hubManager.createdAt.desc();
                        case "updatedAt" -> asc ? hubManager.updatedAt.asc() : hubManager.updatedAt.desc();
                        case "userId" -> asc ? hubManager.userId.asc() : hubManager.userId.desc();
                        case "hubId" -> asc ? hubManager.hubId.asc() : hubManager.hubId.desc();
                        default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다: " + order.getProperty());
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }


}