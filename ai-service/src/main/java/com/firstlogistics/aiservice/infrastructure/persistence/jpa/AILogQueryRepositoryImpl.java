package com.firstlogistics.aiservice.infrastructure.persistence.jpa;


import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.repository.AILogQueryRepository;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AILogQueryRepositoryImpl implements AILogQueryRepository {

    private final JPAQueryFactory queryFactory;
    private static final QAILogJpaEntity aiLog = QAILogJpaEntity.aILogJpaEntity;

    @Override
    public Optional<AILogDetailProjection> findById(UUID aiLogId) {
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
                        aiLog.id.eq(AILogId.of(aiLogId).id()),
                        aiLog.deletedAt.isNull()
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
