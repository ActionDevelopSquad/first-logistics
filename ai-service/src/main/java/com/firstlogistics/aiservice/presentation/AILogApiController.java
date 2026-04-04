package com.firstlogistics.aiservice.presentation;

import com.firstlogistics.aiservice.application.service.AILogCommandService;
import com.firstlogistics.aiservice.presentation.dto.request.AILogCreateRequest;
import com.firstlogistics.aiservice.presentation.dto.response.AILogResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai-logs")
public class AILogApiController {
    private final AILogCommandService aiLogCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<AILogResponse>> createAILog(
            @RequestBody @Valid AILogCreateRequest request
    ) {
        AILogResponse result = AILogResponse.from(aiLogCommandService.createAILog(request.toCommand()));
        return ResponseEntity.status(AILogSuccessCode.AILOG_CREATED.getStatus())
                .body(ApiResponse.success(AILogSuccessCode.AILOG_CREATED, result));
    }

}
