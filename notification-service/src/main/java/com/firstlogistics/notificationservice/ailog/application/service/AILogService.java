package com.firstlogistics.notificationservice.ailog.application.service;

import com.firstlogistics.notificationservice.ailog.application.dto.command.CreateAILogCommand;
import com.firstlogistics.notificationservice.ailog.application.dto.result.AILogResult;
import com.firstlogistics.notificationservice.ailog.application.external.AIPromptGenerator;
import com.firstlogistics.notificationservice.ailog.domain.entity.AILog;
import com.firstlogistics.notificationservice.ailog.domain.enums.AILogStatus;
import com.firstlogistics.notificationservice.ailog.domain.enums.MessengerType;
import com.firstlogistics.notificationservice.ailog.domain.repository.AILogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AILogService {

    private final AILogRepository aiLogRepository;
    private final AIPromptGenerator aiPromptGenerator;

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

        AILog savedLog = aiLogRepository.save(aiLog);

        return AILogResult.from(savedLog);
    }
}
