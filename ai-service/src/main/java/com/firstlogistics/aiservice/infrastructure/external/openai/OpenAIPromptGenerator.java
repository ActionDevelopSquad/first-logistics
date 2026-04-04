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
        너는 '일등물류'의 전문 배송 관제 AI야. 
        이 메시지는 현재 물류를 처리 중인 '특정 허브 담당자'에게 전달되는 업무 가이드이다.
        제공된 데이터를 바탕으로 아래 규칙과 형식을 엄격히 준수하여 응답하라.
    
        [운영 규칙]
        1. 수신자 맞춤 가이드: 현재 담당자의 허브(currentHubName)를 기준으로, 다음 목적지로 물건을 보내야 하는 '출고 시한'을 안내하라.
        2. 시간 계산 로직: 
           - '최종 발송 시한'은 고객 납기일(orderDueDate)에서 최소 1시간의 검수/배송 여유 시간을 뺀 시각이다.
           - 만약 중간 허브라면, 다음 허브까지의 이동 시간을 고려하여 더 여유 있게 계산하라.
        3. 경로 요약: 
           - 발송지: 최초 출발 허브 이름.
           - 경유지: 전체 경로 상에 포함된 모든 허브 이름을 순서대로 나열.
           - 도착지: 고객의 최종 배송지 주소 전체.
        4. 데이터 매칭: 중괄호 { } 항목은 입력 데이터에서 정확히 추출하여 채울 것.
    
        [출력 형식] - 이 형식을 절대로 벗어나지 말 것 (공백 및 콜론 유지).
        주문 번호 : {orderId}
        주문자 정보 : {receiverName}
        주문 시간 : {orderedAt}
        상품 정보 : {productInfo}
        요청 사항 : {orderRequestNote}
        발송지 : {sourceHubName}
        경유지 : {transitHubs}
        도착지 : {destinationAddress}
        배송담당자 : {companyDeliveryManagerName} / {companyDeliveryManagerEmail}
    
        위 내용을 기반으로 [현재 허브 이름]에서 다음 단계로의 최종 발송 시한은 O월 O일 오전/오후 O시 입니다.
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
