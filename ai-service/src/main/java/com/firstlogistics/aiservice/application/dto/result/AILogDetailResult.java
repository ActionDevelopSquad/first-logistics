package com.firstlogistics.aiservice.application.dto.result;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;

import java.time.LocalDateTime;
import java.util.UUID;

public record AILogDetailResult(
        UUID id,
        MessengerMessageId messageId,
        MessengerType messengerType,
        String requestContent,
        String responseContent,
        String systemPrompt,
        AILogStatus status,
        LocalDateTime createdAt
    ) {

    public static AILogDetailResult from(AILogDetailProjection info) {
        return new AILogDetailResult(
                info.id(),
                info.messageId(),
                info.messengerType(),
                info.requestContent(),
                info.responseContent(),
                info.systemPrompt(),
                info.status(),
                info.createdAt()
        );
    }
}
