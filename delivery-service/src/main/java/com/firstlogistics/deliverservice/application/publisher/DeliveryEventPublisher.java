package com.firstlogistics.deliverservice.application.publisher;

import com.firstlogistics.deliverservice.domain.event.DeliveryCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryEventPublisher {

	private final ApplicationEventPublisher applicationEventPublisher;

	public void publishedDeliveryCreated(DeliveryCreatedEvent event) {
		applicationEventPublisher.publishEvent(event);
		log.debug("DeliveryCreatedEvent 등록 - deliveryId: {}", event.delivery().deliveryId());
	}
}
