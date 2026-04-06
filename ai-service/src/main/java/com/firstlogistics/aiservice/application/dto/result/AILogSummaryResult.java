package com.firstlogistics.aiservice.application.dto.result;

import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.projection.AILogSummaryProjection;

import java.util.UUID;

public record AILogSummaryResult (
        UUID id,
        UUID messageId,
        AILogStatus status,
        MessengerType messengerType,
        String responseContent
) {
    public static AILogSummaryResult fromSummary(AILogSummaryProjection dto) {
        return new AILogSummaryResult(
                dto.aiLogId().id(),
                dto.messageId().id(),
                dto.status(),
                dto.messengerType(),
                dto.responseContent()
        );
    }
}
