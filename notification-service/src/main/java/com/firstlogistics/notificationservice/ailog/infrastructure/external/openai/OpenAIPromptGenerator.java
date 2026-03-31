package com.firstlogistics.notificationservice.ailog.infrastructure.external.openai;

import com.firstlogistics.notificationservice.ailog.application.dto.command.CreateAILogCommand;
import com.firstlogistics.notificationservice.ailog.application.external.AIPromptGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.ai.chat.client.ChatClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAIPromptGenerator implements AIPromptGenerator {
    private final ChatClient chatClient;

    // 시스템 프롬프트를 상수로 관리하여 일관성 유지
    private static final String SYSTEM_PROMPT = """
            너는 '일등물류'의 배송 관제 AI야. 다음 규칙을 엄수해:
            1. 고객 요청 시간보다 '최소 1시간 일찍' 도착하도록 계산한다.
            2. 배송 담당자의 근무 시간 내에서만 이동 가능하다.
            3. 경유지(허브) 개수에 따른 지연 시간을 고려한다.
            4. 마지막에 반드시 '최종 발송 시한은 O월 O일 오전/오후 O시 입니다.'를 포함한다.
            """;

    @Override
    public String generateDeliveryGuide(CreateAILogCommand command) {
        // 주문 데이터를 문자열로 조립 (Request Content 생성)
        String userContent = command.toRawText();

        log.info("AI 배송 가이드 생성 요청 시작 - 주문번호: {}", command.orderId());

        // AI 호출 및 결과 반환
        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(userContent)
                .call()
                .content();
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }
}
