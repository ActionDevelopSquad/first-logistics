package com.firstlogistics.hubservice.hubconnection.application.listener;

import com.firstlogistics.hubservice.hub.domain.event.HubActivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeactivatedEvent;
import com.firstlogistics.hubservice.hub.domain.event.HubDeletedEvent;
import com.firstlogistics.hubservice.hubconnection.application.HubConnectionCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class HubEventListener {
    private final HubConnectionCommandService commandService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHubActivatedEvent(HubActivatedEvent event) {
        try {
            commandService.activateByHub(event.hubId());
        } catch (Exception e) {
            log.error("Failed to activate hub connections for hubId: {}", event.hubId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHubDeactivatedEvent(HubDeactivatedEvent event) {
        try {
            commandService.deactivateByHub(event.hubId());
        } catch (Exception e) {
            log.error("Failed to deactivate hub connections for hubId: {}", event.hubId(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleHubDeletedEvent(HubDeletedEvent event) {
        try {
            commandService.deleteByHub(event.hubId());
        } catch (Exception e) {
            log.error("Failed to delete hub connections for hubId: {}", event.hubId(), e);
        }
    }
}
