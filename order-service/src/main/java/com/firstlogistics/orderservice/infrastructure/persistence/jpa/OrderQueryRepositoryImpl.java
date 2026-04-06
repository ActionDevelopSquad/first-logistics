package com.firstlogistics.orderservice.infrastructure.persistence.jpa;

import com.firstlogistics.orderservice.domain.enums.OrderStatus;
import com.firstlogistics.orderservice.domain.repository.OrderQueryRepository;
import com.firstlogistics.orderservice.domain.repository.dto.OrderSummaryDto;
import com.firstlogistics.orderservice.domain.specification.OrderSearchSpec;
import com.firstlogistics.orderservice.domain.specification.OrderSearchType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static com.firstlogistics.orderservice.infrastructure.persistence.jpa.QOrderJpaEntity.orderJpaEntity;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepositoryImpl implements OrderQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<OrderSummaryDto> findAll(OrderSearchSpec spec, Pageable pageable) {

        List<OrderSummaryDto> content = queryFactory
                .select(Projections.constructor(OrderSummaryDto.class,
                        orderJpaEntity.id,
                        orderJpaEntity.status,
                        orderJpaEntity.totalAmount,
                        orderJpaEntity.supplierCompanyId,
                        orderJpaEntity.supplierManagerId,
                        orderJpaEntity.receiverCompanyId,
                        orderJpaEntity.receiverManagerId,
                        orderJpaEntity.deliveryId,
                        orderJpaEntity.dueDate,
                        orderJpaEntity.createdAt
                ))
                .from(orderJpaEntity)
                .where(
                        orderJpaEntity.deletedAt.isNull(),
                        roleFilter(spec),
                        statusEq(spec.status()),
                        dateBetween(spec.startDate(), spec.endDate()),
                        amountBetween(spec.minAmount(), spec.maxAmount())
                )
                .orderBy(orderJpaEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(orderJpaEntity.count())
                .from(orderJpaEntity)
                .where(
                        orderJpaEntity.deletedAt.isNull(),
                        roleFilter(spec),
                        statusEq(spec.status()),
                        dateBetween(spec.startDate(), spec.endDate()),
                        amountBetween(spec.minAmount(), spec.maxAmount())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression roleFilter(OrderSearchSpec spec) {
        // 마스터 관리자는 모든 주문 조회
        if (spec.isMaster()) return null;

        // 허브 관리자는 담당 허브 주문만
        if (spec.isHubManager()) {
            return orderJpaEntity.supplierHubId.eq(spec.getMyHubId());
        }

        // 업체 관리자는 담당 업체 주문만
        if (spec.isCompanyManager()) {
            UUID userId = spec.getMyUserId();

            if (spec.searchType() == OrderSearchType.SENT) {
                // 공급 주문 조회
                return orderJpaEntity.supplierManagerId.eq(userId);
            } else {
                // 수령 주문 조회 (기본값)
                // null이거나 RECEIVED인 경우
                return orderJpaEntity.receiverManagerId.eq(userId);
            }
        }

        return orderJpaEntity.id.isNull();
    }

    private BooleanExpression statusEq(OrderStatus status) {
        return status != null ? orderJpaEntity.status.eq(status) : null;
    }

    private BooleanExpression dateBetween(LocalDate start, LocalDate end) {
        if (start == null && end == null) return null;
        if (start != null && end == null) return orderJpaEntity.createdAt.goe(start.atStartOfDay());
        if (start == null && end != null) return orderJpaEntity.createdAt.loe(end.atTime(LocalTime.MAX));

        return orderJpaEntity.createdAt.between(start.atStartOfDay(), end.atTime(LocalTime.MAX));
    }

    private BooleanExpression amountBetween(Long min, Long max) {
        if (min == null && max == null) return null;
        if (min != null && max == null) return orderJpaEntity.totalAmount.goe(min);
        if (min == null && max != null) return orderJpaEntity.totalAmount.loe(max);

        return orderJpaEntity.totalAmount.between(min, max);
    }
}
