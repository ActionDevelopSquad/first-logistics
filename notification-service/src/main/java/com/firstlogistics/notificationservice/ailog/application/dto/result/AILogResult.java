package com.firstlogistics.notificationservice.ailog.application.dto.result;

import com.firstlogistics.notificationservice.ailog.domain.entity.AILog;
import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;

import java.util.UUID;

public record AILogResult(
        UUID id,
        AILogStatus status,
        MessengerType messengerType
) {
    public static AILogResult from(AILog aiLog) {
        return new AILogResult(
                aiLog.getId().id(),
                aiLog.getStatus(),
                aiLog.getMessengerType()
        );
    }
}