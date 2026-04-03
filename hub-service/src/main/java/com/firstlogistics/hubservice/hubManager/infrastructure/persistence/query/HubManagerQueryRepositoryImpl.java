package com.firstlogistics.hubservice.hubManager.infrastructure.persistence.query;

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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import static com.firstlogistics.hubservice.hubManager.infrastructure.persistence.jpa.QHubManagerJpaEntity.hubManagerJpaEntity;

import java.util.List;
import java.util.UUID;


@RequiredArgsConstructor
@Repository
public class HubManagerQueryRepositoryImpl implements HubManagerQueryRepository {
    private static final QHubManagerJpaEntity hubManager = hubManagerJpaEntity;
    private final JPAQueryFactory queryFactory;
    private final HubManagerMapper mapper;

    @Override
    public HubManager findById(HubManagerId hubManagerId) {
        HubManagerJpaEntity result = queryFactory
                .select(hubManager)
                .from(hubManager)
                .where(hubManager.id.eq(hubManagerId.id()))
                .fetchOne();

        if (result == null)
            throw new HubManagerException(HubManagerErrorCode.HUB_MANAGER_NOT_FOUND);

        return mapper.toDomain(result);
    }

    @Override
    public Page<HubManager> searchByCondition(HubManagerSearchSpec spec, Pageable pageable) {
        List<HubManager> content = queryFactory
                .selectFrom(hubManager)
                .where(
                        userIdsIn(spec.userIds()),
                        hubIdsIn(spec.hubIds())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(HubManagerMapper::toDomain)
                .toList();
        Long total = queryFactory
                .select(hubManager.count())
                .from(hubManager)
                .where(
                        userIdsIn(spec.userIds()),
                        hubIdsIn(spec.hubIds())
                )
                .fetchOne();

        return new PageImpl<>(content,pageable,total == null ? 0 : total);
    }

    @Override
    public UUID findHubIdByUserId(UserId userId) {
        UUID result =  queryFactory
                .select(hubManager.hubId)
                .from(hubManager)
                .where(hubManager.userId.eq(userId.id()))
                .fetchOne();
        if(result == null)
            throw new HubManagerException(HubManagerErrorCode.HUB_MANAGER_NOT_FOUND);

        return result;
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

}