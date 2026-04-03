package com.firstlogistics.aiservice.domain.projection;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;

import java.time.LocalDateTime;
import java.util.UUID;

public record AILogDetailProjection(
        UUID id,
        MessengerMessageId messageId,
        MessengerType messengerType,
        String requestContent,
        String responseContent,
        String systemPrompt,
        AILogStatus status,
        LocalDateTime createdAt
    ) {
}
