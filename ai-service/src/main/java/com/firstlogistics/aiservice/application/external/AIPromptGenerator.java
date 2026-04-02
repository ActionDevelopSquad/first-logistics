package com.firstlogistics.aiservice.application.external;

import com.firstlogistics.aiservice.application.dto.command.CreateAILogCommand;

public interface AIPromptGenerator {

    String generateDeliveryGuide(CreateAILogCommand command);

    String getSystemPrompt();
}
