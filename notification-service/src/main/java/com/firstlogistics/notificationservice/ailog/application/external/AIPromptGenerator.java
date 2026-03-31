package com.firstlogistics.notificationservice.ailog.application.external;

import com.firstlogistics.notificationservice.ailog.application.dto.command.CreateAILogCommand;

public interface AIPromptGenerator {

    String generateDeliveryGuide(CreateAILogCommand command);

    String getSystemPrompt();
}
