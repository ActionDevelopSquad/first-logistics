package com.firstlogistics.aiservice.application.dto.result;

import com.firstlogistics.aiservice.domain.entity.AILog;
import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;

import java.util.UUID;

public record AILogResult(
        UUID id,
        UUID messageId,
        AILogStatus status,
        MessengerType messengerType,
        String responseContent
) {
    public static AILogResult from(AILog aiLog) {
        return new AILogResult(
                aiLog.getId().id(),
                aiLog.getMessageId().id(),
                aiLog.getStatus(),
                aiLog.getMessengerType(),
                aiLog.getResponseContent()
        );
    }
}