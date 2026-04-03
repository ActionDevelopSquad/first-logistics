package com.firstlogistics.aiservice.presentation.dto.response;

import com.firstlogistics.aiservice.application.dto.result.AILogDetailResult;
import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AILogDetailResponse(
        UUID id,
        AILogStatus status,
        MessengerType messengerType,
        String requestContent,
        String responseContent,
        String systemPrompt,
        LocalDateTime createdAt
) {
    public static AILogDetailResponse from(AILogDetailResult result) {
        return new AILogDetailResponse(
                result.id(),
                result.status(),
                result.messengerType(),
                result.requestContent(),
                result.responseContent(),
                result.systemPrompt(),
                result.createdAt()
        );
    }
}