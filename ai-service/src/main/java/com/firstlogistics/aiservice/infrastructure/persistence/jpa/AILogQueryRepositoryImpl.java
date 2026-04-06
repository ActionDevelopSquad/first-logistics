package com.firstlogistics.aiservice.infrastructure.persistence.jpa;

import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.repository.AILogQueryRepository;
import com.firstlogistics.aiservice.domain.repository.dto.AILogSearchDto;
import com.firstlogistics.aiservice.domain.repository.dto.AILogSummaryDto;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AILogQueryRepositoryImpl implements AILogQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QAILogJpaEntity aiLog = QAILogJpaEntity.aILogJpaEntity;

    @Override
    public Optional<AILogDetailProjection> findById(AILogId aiLogId) {
        AILogDetailProjection result = queryFactory
                .select(Projections.constructor(AILogDetailProjection.class,
                        aiLog.id,
                        aiLog.messageId,
                        aiLog.messengerType,
                        aiLog.requestContent,
                        aiLog.responseContent,
                        aiLog.systemPrompt,
                        aiLog.status,
                        aiLog.createdAt
                ))
                .from(aiLog)
                .where(
                        aiLog.id.eq(aiLogId.id()),
                        aiLog.deletedAt.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<AILogSummaryDto> searchByCondition(AILogSearchDto condition, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(aiLog.deletedAt.isNull());

        if (condition != null) {
            if (condition.messageId() != null) {
                // messageId가 UUID나 객체라면 eq를 사용해야 합니다.
                builder.and(aiLog.messageId.eq(condition.messageId()));
            }
            if (condition.status() != null) {
                builder.and(aiLog.status.eq(condition.status()));
            }
            if (condition.messengerType() != null) {
                builder.and(aiLog.messengerType.eq(condition.messengerType()));
            }
            if (condition.startDate() != null && condition.endDate() != null) {
                builder.and(aiLog.createdAt.between(condition.startDate(), condition.endDate()));
            }
        }

        // 3. 데이터 조회 쿼리
        List<AILogSummaryDto> content = queryFactory
                .select(Projections.constructor(
                        AILogSummaryDto.class,
                        aiLog.id,
                        aiLog.messageId,
                        aiLog.status,
                        aiLog.messengerType,
                        aiLog.responseContent
                ))
                .from(aiLog)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(aiLog.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(aiLog.count())
                .from(aiLog)
                .where(builder);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
