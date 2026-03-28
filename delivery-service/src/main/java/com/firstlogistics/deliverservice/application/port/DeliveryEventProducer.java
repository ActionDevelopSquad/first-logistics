package com.firstlogistics.deliverservice.application.port;

import com.firstlogistics.deliverservice.domain.entity.Delivery;

public interface DeliveryEventProducer {

	void sendCreated(Delivery delivery);
}
