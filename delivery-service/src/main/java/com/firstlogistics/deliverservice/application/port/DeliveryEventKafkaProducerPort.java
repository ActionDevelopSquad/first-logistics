package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.domain.event.DeliveryCreatedEvent;
import com.firstlogistics.deliverservice.domain.event.DeliveryCreationFailedEvent;

public interface DeliveryEventKafkaProducerPort {

	void handleDeliveryCreated(DeliveryCreatedEvent event);

	void handleDeliveryCreationFailed(DeliveryCreationFailedEvent event);

	void handleOrderAcceptedDlt(String key, Object value);
}
