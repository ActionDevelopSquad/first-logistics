package com.firstlogistics.notificationservice.domain.repository;

import com.firstlogistics.notificationservice.application.dto.query.NotificationSearchQuery;
import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.projection.NotificationSummaryProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationRepository {
    Notification save(Notification entity);

    Page<NotificationSummaryProjection> searchByCondition(NotificationSearchQuery query, Pageable pageable);
}
