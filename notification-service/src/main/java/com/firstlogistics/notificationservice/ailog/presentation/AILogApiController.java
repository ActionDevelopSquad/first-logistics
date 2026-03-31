package com.firstlogistics.notificationservice.ailog.presentation;

import com.firstlogistics.notificationservice.ailog.application.service.AILogService;
import com.firstlogistics.notificationservice.ailog.presentation.dto.request.AILogCreateRequest;
import com.firstlogistics.notificationservice.ailog.presentation.dto.response.AILogResponse;
import common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai-logs")
public class AILogApiController {
    private final AILogService aiLogService;

    @PostMapping
    public ResponseEntity<ApiResponse<AILogResponse>> createAILog(
            @RequestBody AILogCreateRequest request
    ) {
        AILogResponse result = AILogResponse.from(aiLogService.createAILog(request.toCommand()));
        return ResponseEntity.status(AILogSuccessCode.AILOG_CREATED.getStatus())
                .body(ApiResponse.success(AILogSuccessCode.AILOG_CREATED, result));
    }

}
