package com.firstlogistics.userservice.infrastructure.event;

import com.firstlogistics.userservice.domain.event.UserEvents;
import common.event.Events;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventsImpl implements UserEvents {

    @Override
    public void publish(Object event) {
        Events.trigger(event);
    }
}
