package com.firstlogistics.aiservice.application.service;

import com.firstlogistics.aiservice.application.dto.command.CreateAILogCommand;
import com.firstlogistics.aiservice.application.dto.result.AILogResult;
import com.firstlogistics.aiservice.application.external.AIPromptGenerator;
import com.firstlogistics.aiservice.domain.entity.AILog;
import com.firstlogistics.aiservice.domain.enums.AILogStatus;
import com.firstlogistics.aiservice.domain.enums.MessengerType;
import com.firstlogistics.aiservice.domain.repository.AILogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AILogCommandServiceTest {

    @InjectMocks
    private AILogCommandService aiLogCommandService;

    @Mock
    private AILogRepository aiLogRepository;

    @Mock
    private AIPromptGenerator aiPromptGenerator;

    @Test
    @DisplayName("AI 로그 생성 시 AI 응답을 받고 성공 상태로 DB에 저장된다")
    void createAILog_Success() {
        // 1. Given: 테스트용 커맨드 데이터 생성
        UUID orderId = UUID.randomUUID();
        CreateAILogCommand command = new CreateAILogCommand(
                orderId,
                "김철수",
                "chulsoo@example.com",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                "마른 오징어 50개",
                "빨리주세요",
                "서울 센터",
                "대전 센터",
                "부산 주소",
                "고길동",
                "kdk@test.com",
                "slack_id"
        );

        String mockAiResponse = "최종 발송 시한은 4월 4일입니다.";
        String mockSystemPrompt = "시스템 프롬프트 내용";

        // 외부 인터페이스 모킹
        given(aiPromptGenerator.generateDeliveryGuide(any(CreateAILogCommand.class)))
                .willReturn(mockAiResponse);
        given(aiPromptGenerator.getSystemPrompt())
                .willReturn(mockSystemPrompt);

        // 저장될 엔티티 모킹 (실제 AILog.create 로직 모사)
        AILog aiLog = AILog.create(
                command.toRawText(), mockAiResponse, mockSystemPrompt,
                AILogStatus.SUCCESS, MessengerType.SLACK
        );

        // Repository 저장 결과 반환 설정 (AILogResult.from에서 id().id()를 호출하므로 구조 맞춰줌)
        given(aiLogRepository.save(any(AILog.class))).willReturn(aiLog);

        // 2. When: 서비스 실행
        AILogResult result = aiLogCommandService.createAILog(command);

        // 3. Then: 결과 검증
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo(AILogStatus.SUCCESS);
        assertThat(result.messengerType()).isEqualTo(MessengerType.SLACK);

        // 의존성 호출 횟수 검증
        verify(aiPromptGenerator, times(1)).generateDeliveryGuide(any());
        verify(aiLogRepository, times(1)).save(any());
    }
}