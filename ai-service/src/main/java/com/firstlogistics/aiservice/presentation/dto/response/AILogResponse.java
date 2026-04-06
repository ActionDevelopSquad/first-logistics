package com.firstlogistics.aiservice.presentation.dto.response;

import com.firstlogistics.aiservice.application.dto.result.AILogResult;
import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;

import java.util.UUID;

public record AILogResponse(
        UUID id,
        AILogStatus status,
        MessengerType messengerType,
        String responseContent
) {
    public static AILogResponse from(AILogResult result) {
        return new AILogResponse(
                result.id(),
                result.status(),
                result.messengerType(),
                result.responseContent()
        );
    }
}