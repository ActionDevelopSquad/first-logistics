package com.firstlogistics.aiservice.domain.event;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DeliveryAcceptedEvent(
        OrderInfo order,
        DeliveryInfo delivery,
        UUID currentHubId
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OrderInfo(
            UUID orderId,
            LocalDateTime orderedAt,
            LocalDateTime orderDueDate,
            String orderRequestNote,
            List<OrderItemInfo> orderItems
    ) {

    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OrderItemInfo(
            UUID productId,
            String productName,
            int quantity,
            Long price
    ) {

    }
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeliveryInfo(
            UUID deliveryId,
            String receiverName,
            String receiverSlackId,
            String receiverRoadAddress,
            String receiverDetailAddress,
            List<DeliveryRouteInfo> deliveryRoutes,
            String companyDeliveryManagerSlackId,
            String companyDeliveryManagerName,
            String companyDeliveryManagerPhone,
            String companyDeliveryManagerEmail
    ) {

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record DeliveryRouteInfo(
        int sequence,
        UUID sourceHubId,
        String sourceHubName,
        String sourceHubRoadAddress,
        UUID destinationHubId,
        String destinationHubName,
        String destinationHubRoadAddress,
        int estimatedDistanceMeters,
        int estimatedDurationMinutes,
        String hubDeliveryManagerSlackId
    ) {

    }
}
