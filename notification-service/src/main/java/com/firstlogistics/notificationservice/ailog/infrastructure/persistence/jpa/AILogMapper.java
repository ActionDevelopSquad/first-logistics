package com.firstlogistics.notificationservice.ailog.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.ailog.domain.entity.AILog;
import com.firstlogistics.notificationservice.ailog.domain.vo.AILogId;
import com.firstlogistics.notificationservice.ailog.domain.vo.MessengerMessageId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AILogMapper {

    // Domain → Entity 변환
    public AILogJpaEntity toEntity(AILog aiLog) {
        return new AILogJpaEntity(
                aiLog.getId().id(),
                Optional.ofNullable(aiLog.getMessageId())  // messageId가 나중에 대입됨
                        .map(MessengerMessageId::id)
                        .orElse(null),
                aiLog.getRequestContent(),
                aiLog.getResponseContent(),
                aiLog.getSystemPrompt(),
                aiLog.getStatus(),
                aiLog.getMessengerType()
        );
    }

    // Entity → Domain 변환
    public AILog toDomain(AILogJpaEntity entity) {
        return AILog.reconstitute(
                AILogId.of(entity.getId()),
                Optional.ofNullable(entity.getMessageId())  // messageId가 나중에 대입됨
                        .map(MessengerMessageId::of)
                        .orElse(null),
                entity.getRequestContent(),
                entity.getResponseContent(),
                entity.getSystemPrompt(),
                entity.getStatus(),
                entity.getMessengerType()
        );
    }
}
