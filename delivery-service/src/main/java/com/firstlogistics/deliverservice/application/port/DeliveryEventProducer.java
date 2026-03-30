package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.infrastructure.messaging.producer.event.DeliveryCreatedEvent;

public interface DeliveryEventProducer {

	void sendCreated(DeliveryCreatedEvent deliveryCreatedEvent);
}
