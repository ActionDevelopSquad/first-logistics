package com.firstlogistics.deliverservice.application.dto.command;

import com.firstlogistics.deliverservice.infrastructure.messaging.consumer.event.OrderAcceptedEvent;

import java.util.UUID;

public record CreateDeliveryCommand(
	UUID orderId,
	UUID sourceHubId,
	UUID receiverCompanyId,
	UUID receiverId,
	String roadAddress,
	String detailAddress,
	double latitude,
	double longitude
) {
	public static CreateDeliveryCommand create(OrderAcceptedEvent event){
		return new CreateDeliveryCommand(
			event.orderId(),
			event.sourceHubId(),
			event.receiverCompanyId(),
			event.receiverId(),
			event.roadAddress(),
			event.detailAddress(),
			event.latitude(),
			event.longitude()
		);
	}
}
