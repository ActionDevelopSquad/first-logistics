package com.firstlogistics.notificationservice.domain.client;

import com.firstlogistics.notificationservice.domain.enums.MessengerType;

public interface NotificationClient {
    void send(String slackId, String content);

    boolean support(MessengerType type);
}
