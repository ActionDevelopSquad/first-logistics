package com.firstlogistics.notificationservice.ailog.infrastructure.persistence.jpa;

import com.firstlogistics.notificationservice.ailog.domain.entity.AILog;
import com.firstlogistics.notificationservice.ailog.domain.vo.AILogId;
import com.firstlogistics.notificationservice.ailog.domain.vo.MessengerMessageId;
import org.springframework.stereotype.Component;

@Component
public class AILogMapper {

    // Domain → Entity 변환
    public AILogJpaEntity toEntity(AILog aiLog) {
        return new AILogJpaEntity(
                aiLog.getId().id(),
                aiLog.getMessageId().id(),
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
                MessengerMessageId.of(entity.getMessageId()),
                entity.getRequestContent(),
                entity.getResponseContent(),
                entity.getSystemPrompt(),
                entity.getStatus(),
                entity.getMessengerType()
        );
    }
}
