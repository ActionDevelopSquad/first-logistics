package com.firstlogistics.notificationservice.application.service;

import com.firstlogistics.notificationservice.application.dto.query.NotificationSearchQuery;
import com.firstlogistics.notificationservice.application.dto.result.NotificationDetailResult;
import com.firstlogistics.notificationservice.application.dto.result.NotificationSummaryResult;
import com.firstlogistics.notificationservice.domain.exception.NotificationErrorCode;
import com.firstlogistics.notificationservice.domain.exception.NotificationException;
import com.firstlogistics.notificationservice.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationRepository notificationRepository;

    public Page<NotificationSummaryResult> searchNotifications(
            NotificationSearchQuery query,
            Pageable pageable
    ) {

        return notificationRepository.searchByCondition(query, pageable)
                .map(NotificationSummaryResult::from);
    }

    public NotificationDetailResult getNotification(UUID notificationId) {
        return notificationRepository.findDetailById(notificationId)
                .map(NotificationDetailResult::from)
                .orElseThrow(() -> new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND));
    }
}