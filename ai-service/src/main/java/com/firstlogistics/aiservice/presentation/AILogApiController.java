package com.firstlogistics.aiservice.presentation;

import com.firstlogistics.aiservice.application.service.AILogCommandService;
import com.firstlogistics.aiservice.application.service.AILogQueryService;
import com.firstlogistics.aiservice.presentation.dto.request.AILogCreateRequest;
import com.firstlogistics.aiservice.presentation.dto.request.SearchAILogsRequest;
import com.firstlogistics.aiservice.presentation.dto.response.AILogDetailResponse;
import com.firstlogistics.aiservice.presentation.dto.response.AILogPageResponse;
import com.firstlogistics.aiservice.presentation.dto.response.AILogResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/{aiLogId}")
    public ResponseEntity<ApiResponse<AILogDetailResponse>> getAILog(
            @PathVariable UUID aiLogId
    ) {

        AILogDetailResponse result = AILogDetailResponse.from(aiLogQueryService.getAILog(aiLogId));

        return ResponseEntity.ok(ApiResponse.success(AILogSuccessCode.AILOG_FOUND, result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AILogPageResponse>> search(
            @Valid @ModelAttribute SearchAILogsRequest request,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        AILogPageResponse response = AILogPageResponse.from(
                aiLogQueryService.searchAILogs(request.toQuery(), pageable)
        );
        return ResponseEntity.ok(ApiResponse.success(AILogSuccessCode.AILOG_LIST_FOUND, response));
    }

    @DeleteMapping("/{aiLogId}")
    public ResponseEntity<ApiResponse<Void>> deleteAILog(
            @PathVariable UUID aiLogId,
            @RequestHeader("X-User-Id") UUID userId
    ) {
        aiLogCommandService.deleteAILog(aiLogId, userId);
        // 데이터가 없으므로 null을 넘겨줍니다.
        return ResponseEntity.ok(ApiResponse.success(AILogSuccessCode.AILOG_DELETED, null));
    }
}
