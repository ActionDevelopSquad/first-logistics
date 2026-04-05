package com.firstlogistics.hubservice.hubManager.infrastructure.messaging.consumer;


import com.firstlogistics.hubservice.hubManager.application.HubManagerCommandService;
import com.firstlogistics.hubservice.hubManager.application.dto.command.CreateHubManagerCommand;
import com.firstlogistics.hubservice.hubManager.domain.event.UserHubManagerStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserHubManagerStatusChangedConsumer {
    private final HubManagerCommandService hubManagerCommandService;

    @KafkaListener(
            topics = "user.hub.status.changed",
            containerFactory = "userHubManagerStatusChangedListenerContainerFactory"
    )
    public void consume(UserHubManagerStatusChangedEvent event, Acknowledgment ack){
        if(event.shouldCreateHubManager())
            hubManagerCommandService.createHubManager(CreateHubManagerCommand.from(event));
        ack.acknowledge();
    }
}
