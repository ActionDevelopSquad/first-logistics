package com.firstlogistics.aiservice.application.facade;

import com.firstlogistics.aiservice.application.dto.command.CreateAILogCommand;
import com.firstlogistics.aiservice.application.service.AILogService;
import com.firstlogistics.aiservice.domain.event.DeliveryAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIMessageFacade {
    private final AILogService aiLogService;

    public void processAiNotification(DeliveryAcceptedEvent event) {
        // 1. 경유지 정보 조립 (예: 대전 센터, 부산 센터)
        String hubs = event.delivery().deliveryRoutes().stream()
                .map(DeliveryAcceptedEvent.DeliveryRouteInfo::sourceHubName)
                .distinct()
                .collect(Collectors.joining(", "));

        // 2. 상품 정보 조립 (예: 오징어 50박스)
        String productInfo = event.order().orderItems().stream()
                .map(item -> item.productName() + " " + item.quantity() + "개")
                .collect(Collectors.joining(", "));

        // 3. Command 생성
        CreateAILogCommand command = new CreateAILogCommand(
                event.order().orderId(),
                event.delivery().receiverName(),
                "customer@example.com", // 필요시 이벤트에 필드 추가
                event.order().orderedAt(),
                productInfo,
                event.order().orderRequestNote(),
                event.delivery().deliveryRoutes().getFirst().sourceHubName(), // 첫 출발지
                hubs,
                event.delivery().receiverRoadAddress() + " " + event.delivery().receiverDetailAddress(),
                event.delivery().companyDeliveryStaffName(),
                event.delivery().companyDeliveryStaffEmail(),
                event.delivery().receiverSlackId()
        );

        aiLogService.createAILog(command);
    }
}
