package com.firstlogistics.aiservice.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAILogCommand(
        UUID orderId,
        String customerName,
        String customerEmail,
        LocalDateTime orderedAt,
        LocalDateTime orderDueDate,
        String productInfo,
        String requirement,
        String departure,
        String hubs,
        String destination,
        String deliveryStaffName,
        String deliveryStaffEmail,
        String hubManagerSlackId
) {

    // Generator에서 사용하기 편하게 텍스트로 변환하는 헬퍼 메서드
    public String toRawText() {
        return String.format("""
                주문 번호 : %s
                주문자 정보 : %s / %s
                주문 시간 : %s
                상품 정보 : %s
                요청 사항 : %s
                발송지 : %s
                경유지 : %s
                도착지 : %s
                배송담당자 : %s / %s
                발송 시한 : %s
                """,
                orderId, customerName, customerEmail, orderedAt,
                productInfo, requirement, departure, hubs,
                destination, deliveryStaffName, deliveryStaffEmail, orderDueDate);
    }
}