package com.firstlogistics.aiservice.domain.projection;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.vo.AILogId;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;

import java.time.LocalDateTime;

public record AILogDetailProjection(
        AILogId id,
        MessengerMessageId messageId,
        MessengerType messengerType,
        String requestContent,
        String responseContent,
        String systemPrompt,
        AILogStatus status,
        LocalDateTime createdAt
    ) {
}
