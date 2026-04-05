package com.firstlogistics.aiservice.application.dto.result;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.projection.AILogDetailProjection;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;

import java.time.LocalDateTime;

public record AILogDetailResult(
        AILogId id,
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
