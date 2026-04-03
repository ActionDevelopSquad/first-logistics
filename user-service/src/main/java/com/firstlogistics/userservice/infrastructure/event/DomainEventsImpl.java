package com.firstlogistics.userservice.infrastructure.event;

import com.firstlogistics.userservice.domain.event.DomainEvent;
import com.firstlogistics.userservice.domain.event.UserEvents;
import common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventsImpl implements DomainEvent {

    @Override
    public void publish(UserEvents event) {
        Events.trigger(event);
    }
}
