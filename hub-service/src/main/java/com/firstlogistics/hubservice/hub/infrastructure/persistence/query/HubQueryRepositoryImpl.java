package com.firstlogistics.hubservice.hub.infrastructure.persistence.query;

import com.firstlogistics.hubservice.hub.domain.exception.HubErrorCode;
import com.firstlogistics.hubservice.hub.domain.exception.HubException;
import com.firstlogistics.hubservice.hub.domain.repository.HubQueryRepository;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubDetailsDto;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSearchDto;
import com.firstlogistics.hubservice.hub.domain.repository.dto.HubSummaryDto;
import com.firstlogistics.hubservice.hub.domain.specification.HubIdsSpec;
import com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa.QHubJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import static com.firstlogistics.hubservice.hub.infrastructure.persistence.jpa.QHubJpaEntity.hubJpaEntity;

@Repository
@RequiredArgsConstructor
public class HubQueryRepositoryImpl implements HubQueryRepository {
    private static final QHubJpaEntity hub = hubJpaEntity;

    private final JPAQueryFactory queryFactory;

    @Override
    public HubDetailsDto findById(UUID hubId) {
        HubDetailsDto result = queryFactory
                .select(Projections.constructor(
                HubDetailsDto.class,
                        hub.id,
                        hub.name,
                        hub.roadAddress,
                        hub.latitude,
                        hub.longitude,
                        hub.status,
                        hub.createdAt,
                        hub.updatedAt
                )).from(hub)
                .where(
                        hub.id.eq(hubId),
                        notDeleted()
                ).fetchOne();

        if(result == null)
            throw new HubException(HubErrorCode.HUB_NOT_FOUND);
        return result;
    }

    @Override
    public Page<HubSummaryDto> searchByCondition(HubSearchDto hubSearchDto, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(notDeleted());

        if(hubSearchDto != null){
            if(StringUtils.hasText(hubSearchDto.name())){
                builder.and(hub.name.containsIgnoreCase(hubSearchDto.name()));
            }
            if(hubSearchDto.status()!= null){
                builder.and(hub.status.eq(hubSearchDto.status()));
            }
        }
            List<HubSummaryDto> content = queryFactory
                    .select(Projections.constructor(
                            HubSummaryDto.class,
                            hub.id,
                            hub.name,
                            hub.roadAddress,
                            hub.status
                    )).from(hub)
                    .where(builder)
                    .orderBy(getOrderSpecifiers(hubSearchDto))
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            JPAQuery<Long> countQuery = queryFactory
                    .select(hub.count())
                    .from(hub)
                    .where(builder);

            return PageableExecutionUtils.getPage(content, pageable,countQuery::fetchOne);
    }

    @Override
    public UUID findNearest(double latitude, double longitude) {
        UUID hubId = queryFactory
                .select(hub.id)
                .from(hub)
                .where(notDeleted())
                .orderBy(distanceExpression(latitude,longitude).asc())
                .fetchFirst();
        if(hubId == null)
            throw new HubException(HubErrorCode.HUB_NOT_FOUND);
        return hubId;
    }

    @Override
    public List<HubSummaryDto> findAllByIds(HubIdsSpec spec) {
        if(spec == null || spec.ids() == null || spec.ids().isEmpty())
            return null;

        return queryFactory
                .select(Projections.constructor(
                        HubSummaryDto.class,
                        hub.id,
                        hub.name,
                        hub.roadAddress,
                        hub.status
                ))
                .from(hub)
                .where(
                        hub.id.in(spec.ids()),
                        notDeleted()
                )
                .fetch();
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(HubSearchDto dto){
        if(hasCoordinates(dto)){
            return new OrderSpecifier[]{
                    distanceExpression(dto.latitude(), dto.longitude()).asc(),
                    hub.createdAt.desc()
            };
        }
        return new OrderSpecifier[]{
                hub.createdAt.desc()
        };
    }
    private boolean hasCoordinates(HubSearchDto dto){
        return dto != null && dto.latitude() != null && dto.longitude() != null;
    }

    private NumberExpression<Double> distanceExpression(double latitude, double longitude){
        return Expressions.numberTemplate(
                Double.class,
                "POWER({0}-{1},2) + POWER({2}-{3},2)",
                hub.latitude, latitude,
                hub.longitude, longitude
        );
    }
    private BooleanExpression notDeleted(){
        return hub.deletedAt.isNull();
    }
}
