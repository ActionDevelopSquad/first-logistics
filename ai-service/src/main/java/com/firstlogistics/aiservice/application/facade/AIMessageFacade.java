package com.firstlogistics.aiservice.application.facade;

import com.firstlogistics.aiservice.application.dto.command.CreateAILogCommand;
import com.firstlogistics.aiservice.application.service.AILogCommandService;
import com.firstlogistics.aiservice.domain.event.DeliveryAcceptedEvent;
import com.firstlogistics.aiservice.domain.exception.AILogErrorCode;
import com.firstlogistics.aiservice.domain.exception.AILogException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIMessageFacade {
    private final AILogCommandService aiLogCommandService;

    public void processAiNotification(DeliveryAcceptedEvent event) {
        if (event.currentHubId() == null) {
            throw new AILogException(AILogErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 현재 허브 담당자 슬랙 id 추출
        String currentHubManagerSlackId = event.delivery().deliveryRoutes().stream()
                .filter(route -> route.sourceHubId().equals(event.currentHubId()))
                .map(DeliveryAcceptedEvent.DeliveryRouteInfo::hubDeliveryManagerSlackId)
                .findFirst()
                .orElseThrow(() -> new AILogException(AILogErrorCode.INTERNAL_SERVER_ERROR));

        // 경유지 정보 조립 (예: 대전 센터, 부산 센터)
        String hubs = event.delivery().deliveryRoutes().stream()
                .map(DeliveryAcceptedEvent.DeliveryRouteInfo::destinationHubName)
                .distinct()
                .collect(Collectors.joining(", "));

        // 상품 정보 조립 (예: 오징어 50박스)
        String productInfo = event.order().orderItems().stream()
                .map(item -> item.productName() + " " + item.quantity() + "개")
                .collect(Collectors.joining(", "));

        // Command 생성
        CreateAILogCommand command = new CreateAILogCommand(
                event.order().orderId(),
                event.delivery().receiverName(),
                "customer@example.com", // 필요시 이벤트에 필드 추가
                event.order().orderedAt(),
                event.order().orderDueDate(),
                productInfo,
                event.order().orderRequestNote(),
                event.delivery().deliveryRoutes().getFirst().sourceHubName(), // 첫 출발지
                hubs,
                event.delivery().receiverRoadAddress() + " " + event.delivery().receiverDetailAddress(),
                event.delivery().companyDeliveryManagerName(),
                event.delivery().companyDeliveryManagerEmail(),
                currentHubManagerSlackId  // 현재 허브 담당자 슬랙 id
        );

        aiLogCommandService.createAILog(command);
    }
}
