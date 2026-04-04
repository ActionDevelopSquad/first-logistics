package com.firstlogistics.aiservice.presentation;

import com.firstlogistics.aiservice.application.service.AILogCommandService;
import com.firstlogistics.aiservice.application.service.AILogQueryService;
import com.firstlogistics.aiservice.presentation.dto.request.AILogCreateRequest;
import com.firstlogistics.aiservice.presentation.dto.response.AILogDetailResponse;
import com.firstlogistics.aiservice.presentation.dto.response.AILogResponse;
import common.response.ApiResponse;
//import common.security.security.aop.OnlyMaster;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai-logs")
public class AILogApiController {
    private final AILogCommandService aiLogCommandService;
    private final AILogQueryService aiLogQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<AILogResponse>> createAILog(
            @RequestBody @Valid AILogCreateRequest request
    ) {
        AILogResponse result = AILogResponse.from(aiLogCommandService.createAILog(request.toCommand()));
        return ResponseEntity.status(AILogSuccessCode.AILOG_CREATED.getStatus())
                .body(ApiResponse.success(AILogSuccessCode.AILOG_CREATED, result));
    }

//    @OnlyMaster
    @GetMapping("/{aiLogId}")
    public ResponseEntity<ApiResponse<AILogDetailResponse>> getAILog(
            @PathVariable UUID aiLogId
    ) {

        AILogDetailResponse result = AILogDetailResponse.from(aiLogQueryService.getAILog(aiLogId));

        return ResponseEntity.ok(ApiResponse.success(AILogSuccessCode.AILOG_FOUND, result));
    }
}
