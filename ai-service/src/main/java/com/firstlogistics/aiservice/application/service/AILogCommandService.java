package com.firstlogistics.aiservice.application.service;

import com.firstlogistics.aiservice.application.dto.command.CreateAILogCommand;
import com.firstlogistics.aiservice.application.dto.result.AILogResult;
import com.firstlogistics.aiservice.application.external.AIPromptGenerator;
import com.firstlogistics.aiservice.domain.entity.AILog;
import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.event.NotificationCreatedEvent;
import com.firstlogistics.aiservice.domain.repository.AILogRepository;
import com.firstlogistics.aiservice.domain.vo.MessengerMessageId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AILogCommandService {

    private final AILogRepository aiLogRepository;
    private final AIPromptGenerator aiPromptGenerator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AILogResult createAILog(CreateAILogCommand command) {

        String responseContent = aiPromptGenerator.generateDeliveryGuide(command);
        String systemPrompt = aiPromptGenerator.getSystemPrompt();

        AILog aiLog = AILog.create(
                command.toRawText(),        // 1. 요청 본문
                responseContent,            // 2. AI 응답 결과
                systemPrompt,               // 3. 사용된 시스템 프롬프트
                AILogStatus.SUCCESS,        // 4. 상태 (성공)
                MessengerType.SLACK         // 5. 메신저 타입
        );

//        AILog savedLog = aiLogRepository.save(aiLog);
        AILogResult aiLogResult = AILogResult.from(aiLogRepository.save(aiLog));

        eventPublisher.publishEvent(NotificationCreatedEvent.of(
                aiLogResult.messageId(),
                command.hubManagerSlackId(),
                aiLogResult.responseContent(),
                aiLogResult.messengerType()
        ));

        return aiLogResult;
    }
}
