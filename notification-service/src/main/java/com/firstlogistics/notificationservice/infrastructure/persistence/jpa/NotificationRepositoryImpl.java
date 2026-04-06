package com.firstlogistics.notificationservice.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.application.dto.query.NotificationSearchQuery;
import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.domain.projection.NotificationSummaryProjection;
import com.firstlogistics.notificationservice.domain.repository.NotificationRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.firstlogistics.notificationservice.infrastructure.persistence.jpa.QNotificationJpaEntity.notificationJpaEntity;

@Repository
@RequiredArgsConstructor
@Slf4j
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;
    private final NotificationMapper notificationMapper;
    private final JPAQueryFactory queryFactory;

    @Override
    public Notification save(Notification notification) {
        try {
            // 1. Domain -> Entity 변환 및 저장
            NotificationJpaEntity entity = notificationMapper.toEntity(notification);
            NotificationJpaEntity savedEntity = notificationJpaRepository.save(entity);

            log.info("알림 저장 완료: ID = {}", savedEntity.getId());

            // 2. Entity -> Domain 변환 후 반환
            return notificationMapper.toDomain(savedEntity);

        } catch (DataAccessException e) {
            // AILogRepositoryImpl와 동일하게 데이터베이스 관련 예외 처리
            log.error("알림 저장 중 데이터베이스 에러 발생: user={}, error={}",
                    notification.getReceiverId(), e.getMessage());

            throw new NotificationException(NotificationErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<NotificationSummaryProjection> searchByCondition(NotificationSearchQuery query, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (query != null) {
            if (query.receiverId() != null) {
                builder.and(notificationJpaEntity.receiverId.eq(query.receiverId()));
            }
            if (query.type() != null) {
                builder.and(notificationJpaEntity.type.eq(query.type()));
            }
            if (query.status() != null) {
                builder.and(notificationJpaEntity.status.eq(query.status()));
            }
            if (query.messengerType() != null) {
                builder.and(notificationJpaEntity.messengerType.eq(query.messengerType()));
            }
        }

        List<NotificationSummaryProjection> content = queryFactory
                .select(Projections.constructor(
                        NotificationSummaryProjection.class,
                        notificationJpaEntity.id,
                        notificationJpaEntity.receiverId,
                        notificationJpaEntity.content,
                        notificationJpaEntity.type,
                        notificationJpaEntity.status,
                        notificationJpaEntity.createdAt
                ))
                .from(notificationJpaEntity)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(notificationJpaEntity.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(notificationJpaEntity.count())
                .from(notificationJpaEntity)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
