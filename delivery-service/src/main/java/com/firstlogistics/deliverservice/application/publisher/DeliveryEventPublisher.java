package com.firstlogistics.deliverservice.application.publisher;

import com.firstlogistics.deliverservice.domain.event.DeliveryCreatedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryStatusChangedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryUpdatedEvent;
import common.event.Events;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEventPublisher {

	public void publishedDeliveryCreated(DeliveryCreatedEvent event) {
		Events.trigger(event);
	}

	public void publishDeliveryUpdated(DeliveryUpdatedEvent event) {
		Events.trigger(event);
	}

	public void publishDeliveryStatusChanged(DeliveryStatusChangedEvent event) {
		Events.trigger(event);
	}
}
