package com.firstlogistics.notificationservice.domain.repository;

import com.firstlogistics.notificationservice.application.dto.query.NotificationSearchQuery;
import com.firstlogistics.notificationservice.domain.entity.Notification;
import com.firstlogistics.notificationservice.domain.projection.NotificationDetailProjection;
import com.firstlogistics.notificationservice.domain.projection.NotificationSummaryProjection;
import com.firstlogistics.notificationservice.domain.vo.NotificationId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification entity);

    Page<NotificationSummaryProjection> searchByCondition(NotificationSearchQuery query, Pageable pageable);

    Optional<NotificationDetailProjection> findDetailById(UUID notificationId);

    Optional<Notification> findById(NotificationId id);

    void delete(Notification domainNotification, UUID userId);
}
