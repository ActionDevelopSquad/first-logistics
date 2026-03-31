package com.firstlogistics.notificationservice.ailog.application.dto.command;

import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;

public record CreateAILogCommand(
        String requestContent,
        String responseContent,
        String systemPrompt,
        AILogStatus status,
        MessengerType messengerType
) {
}