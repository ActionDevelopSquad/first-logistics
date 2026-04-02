package com.firstlogistics.userservice.domain.event;

public interface DomainEvent {
    void publish(UserEvents event);
}