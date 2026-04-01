package com.firstlogistics.notificationservice.ailog.presentation.dto.response;

import com.firstlogistics.notificationservice.ailog.application.dto.result.AILogResult;
import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;

import java.util.UUID;

public record AILogResponse(
        UUID id,
        AILogStatus status,
        MessengerType messengerType
) {
    public static AILogResponse from(AILogResult result) {
        return new AILogResponse(
                result.id(),
                result.status(),
                result.messengerType()
        );
    }
}