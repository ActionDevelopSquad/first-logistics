package com.firstlogistics.notificationservice.domain.repository;

import com.firstlogistics.notificationservice.domain.entity.Notification;

public interface NotificationRepository {
    Notification save(Notification entity);
}
