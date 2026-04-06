package com.firstlogistics.notificationservice.presentation;

import com.firstlogistics.notificationservice.application.dto.result.NotificationSummaryResult;
import com.firstlogistics.notificationservice.application.service.NotificationCommandService;
import com.firstlogistics.notificationservice.application.service.NotificationQueryService;
import com.firstlogistics.notificationservice.presentation.dto.request.SearchNotificationRequest;
import com.firstlogistics.notificationservice.presentation.dto.response.NotificationDetailResponse;
import com.firstlogistics.notificationservice.presentation.dto.response.NotificationPageResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import common.security.aop.OnlyMaster;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final NotificationCommandService notificationCommandService;

    @OnlyMaster
    @GetMapping
    public ResponseEntity<ApiResponse<NotificationPageResponse>> search(
            @Valid @ModelAttribute SearchNotificationRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        // request.toQuery()를 통해 Application 계층의 Query 객체로 변환하여 전달
        Page<NotificationSummaryResult> resultPage = notificationQueryService.searchNotifications(
                request.toQuery(),
                pageable
        );

        // Result Page를 Response Page로 변환
        NotificationPageResponse response = NotificationPageResponse.from(resultPage);

        return ResponseEntity.ok(ApiResponse.success(NotificationSuccessCode.NOTIFICATION_LIST_FOUND, response));
    }

    @OnlyMaster
    @GetMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<NotificationDetailResponse>> getNotification(
            @PathVariable UUID notificationId
    ) {
        NotificationDetailResponse result = NotificationDetailResponse.from(
                notificationQueryService.getNotification(notificationId)
        );

        return ResponseEntity.ok(ApiResponse.success(NotificationSuccessCode.NOTIFICATION_FOUND, result));
    }

    @OnlyMaster
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable UUID notificationId,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        notificationCommandService.deleteNotification(notificationId, userId);
        return ResponseEntity.ok(ApiResponse.success(NotificationSuccessCode.NOTIFICATION_DELETED, null));
    }
}
