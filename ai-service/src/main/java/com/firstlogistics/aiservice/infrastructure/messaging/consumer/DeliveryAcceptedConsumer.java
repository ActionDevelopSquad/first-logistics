package com.firstlogistics.aiservice.infrastructure.messaging.consumer;

import com.firstlogistics.aiservice.application.facade.AIMessageFacade;
import com.firstlogistics.aiservice.domain.event.DeliveryAcceptedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryAcceptedConsumer {
    private final AIMessageFacade aiMessageFacade;

    @KafkaListener(
            topics = "delivery.created",
            groupId = "ai-service-group",
            containerFactory = "deliveryAcceptedListenerContainerFactory"
    )
    public void handleDeliveryCreated(DeliveryAcceptedEvent event, Acknowledgment ack) {

        aiMessageFacade.processAiNotification(event);

        ack.acknowledge();
    }
}
