package com.firstlogistics.aiservice.infrastructure.external.openai;

import com.firstlogistics.aiservice.application.dto.command.CreateAILogCommand;
import com.firstlogistics.aiservice.application.external.AIPromptGenerator;
import com.firstlogistics.aiservice.domain.exception.AILogErrorCode;
import com.firstlogistics.aiservice.domain.exception.AILogException;
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
            너는 '일등물류'의 배송 관제 AI야. 현재 메시지를 받는 대상은 각 경유지의 '허브 담당자'이다.
            다음 규칙과 형식을 엄격히 준수하여 응답하라:

            [운영 규칙]
            1. 시간 계산: 고객 요청 시한(orderDueDate)보다 '최소 1시간 일찍' 도착하도록 계산한다.
            2. 담당자 중심: 현재 단계의 허브 담당자가 알아야 할 발송/경유/도착지 정보를 명확히 한다.
            3. 유연성: 경유지 정보(deliveryRoutes)를 바탕으로 이동 경로를 설명하되, 불필요한 상세 데이터는 생략하고 핵심 동선만 요약한다.
            
            [출력 형식] - 이 형식을 절대로 벗어나지 않을 것.
            주문 번호 : {orderId}
            주문자 정보 : {receiverName}
            주문 시간 : {orderedAt}
            상품 정보 : {productName} {quantity}개
            요청 사항 : {orderRequestNote}
            발송지 : {첫 번째 route의 sourceHubName}
            경유지 : {중간 route들의 destinationHubName들을 쉼표로 연결}
            도착지 : {최종 목적지 주소}
            배송담당자 : {companyDeliveryStaffName} / {companyDeliveryStaffEmail}

            위 내용을 기반으로 도출된 최종 발송 시한은 O월 O일 오전/오후 O시 입니다.
            """;

    @Override
    public String generateDeliveryGuide(CreateAILogCommand command) {
        String userContent = command.toRawText();

        log.info("AI 배송 가이드 생성 요청 시작 - 주문번호: {}", command.orderId());

        try {
            return chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(userContent)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("AI 배송 가이드 생성 실패 - 주문번호: {}", command.orderId(), e);
            throw new AILogException(AILogErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }
}
