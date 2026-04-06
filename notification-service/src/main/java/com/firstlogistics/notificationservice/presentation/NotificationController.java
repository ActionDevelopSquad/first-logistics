package com.firstlogistics.notificationservice.presentation;

import com.firstlogistics.notificationservice.application.dto.result.NotificationSummaryResult;
import com.firstlogistics.notificationservice.application.service.NotificationQueryService;
import com.firstlogistics.notificationservice.presentation.dto.request.SearchNotificationRequest;
import com.firstlogistics.notificationservice.presentation.dto.response.NotificationPageResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationQueryService notificationQueryService;

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
}
