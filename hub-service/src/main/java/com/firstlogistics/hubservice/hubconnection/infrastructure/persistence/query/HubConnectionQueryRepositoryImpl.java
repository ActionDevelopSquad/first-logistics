package com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.query;

import com.firstlogistics.hubservice.hubconnection.domain.entity.HubConnection;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionErrorCode;
import com.firstlogistics.hubservice.hubconnection.domain.exception.HubConnectionException;
import com.firstlogistics.hubservice.hubconnection.domain.repository.HubConnectionQueryRepository;
import com.firstlogistics.hubservice.hubconnection.domain.specification.HubConnectionSpec;
import com.firstlogistics.hubservice.hubconnection.domain.vo.HubConnectionId;
import com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa.HubConnectionJpaEntity;
import com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa.HubConnectionMapper;
import com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa.QHubConnectionJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.firstlogistics.hubservice.hubconnection.infrastructure.persistence.jpa.QHubConnectionJpaEntity.hubConnectionJpaEntity;

@Repository
@RequiredArgsConstructor
public class HubConnectionQueryRepositoryImpl implements HubConnectionQueryRepository {
    private static final QHubConnectionJpaEntity hubConnection = hubConnectionJpaEntity;
    private final HubConnectionMapper mapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public HubConnection findById(HubConnectionId id) {
        HubConnectionJpaEntity connection = queryFactory
                .select(hubConnection)
                .from(hubConnection)
                .where(hubConnection.id.eq(id.id()),
                        notDeleted())
                .fetchOne();
        if(connection == null)
            throw new HubConnectionException(HubConnectionErrorCode.HUB_CONNECTION_NOT_FOUND);

        return mapper.toDomain(connection);
    }

    @Override
    public Page<HubConnection> searchByCondition(HubConnectionSpec spec, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(notDeleted());

        if(spec != null){
            if(spec.status()!= null){
                builder.and(hubConnection.status.eq(spec.status()));
            }
            if(spec.sourceHubId()!=null){
                builder.and(hubConnection.sourceHubId.eq(spec.sourceHubId().id()));
            }
            if(spec.destinationHubId()!=null){
                builder.and(hubConnection.destinationHubId.eq(spec.destinationHubId().id()));
            }
            if(spec.time()!=null){
                builder.and(hubConnection.minutes.eq(spec.time().minutes()));
            }
            if(spec.distance()!=null){
                builder.and(hubConnection.meters.eq(spec.distance().meters()));
            }

        }
        List<HubConnection> content = queryFactory
                .select(hubConnection)
                .from(hubConnection)
                .where(builder)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(mapper::toDomain)
                .toList();

        JPAQuery<Long> countQuery = queryFactory
                .select(hubConnection.count())
                .from(hubConnection)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable,countQuery::fetchOne);

    }

    private BooleanExpression notDeleted(){
        return hubConnection.deletedAt.isNull();
    }
    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return new OrderSpecifier<?>[]{hubConnection.createdAt.desc()};
        }

        return sort.stream()
                .map(order -> {
                    boolean asc = order.isAscending();

                    return switch (order.getProperty()) {
                        case "createdAt" -> asc ? hubConnection.createdAt.asc() : hubConnection.createdAt.desc();
                        case "updatedAt" -> asc ? hubConnection.updatedAt.asc() : hubConnection.updatedAt.desc();
                        case "minutes" -> asc ? hubConnection.minutes.asc() : hubConnection.minutes.desc();
                        case "meters" -> asc ? hubConnection.meters.asc() : hubConnection.meters.desc();
                        case "sourceHubId" -> asc ? hubConnection.sourceHubId.asc() : hubConnection.sourceHubId.desc();
                        case "destinationHubId" -> asc ? hubConnection.destinationHubId.asc() : hubConnection.destinationHubId.desc();
                        default -> throw new HubConnectionException(HubConnectionErrorCode.INVALID_HUB_CONNECTION_SORT);
                    };
                })
                .toArray(OrderSpecifier[]::new);
    }
}
