package com.firstlogistics.hubservice.hubconnection.application.listener;

import com.firstlogistics.hubservice.hub.domain.event.HubActivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeactivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeletedEvent;
import com.firstlogistics.hubservice.hubconnection.application.HubConnectionCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class HubEventListener {
    private final HubConnectionCommandService commandService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHubActivatedEvent(HubActivatedEvent event) {
        commandService.activateByHub(event.hubId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHubDeactivatedEvent(HubDeactivatedEvent event) {
        commandService.deactivateByHub(event.hubId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHubDeletedEvent(HubDeletedEvent event) {
        commandService.deleteByHub(event.hubId());
    }
}
