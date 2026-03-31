package com.firstlogistics.notificationservice.ailog.presentation.dto.request;

import com.firstlogistics.notificationservice.ailog.application.dto.command.CreateAILogCommand;
import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;

public record AILogCreateRequest(
        String requestContent,
        String responseContent,
        String systemPrompt,
        AILogStatus status,
        MessengerType messengerType
) {
    public CreateAILogCommand toCommand() {
        return new CreateAILogCommand(
                requestContent,
                responseContent,
                systemPrompt,
                status,
                messengerType
        );
    }
}