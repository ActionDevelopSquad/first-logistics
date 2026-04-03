package com.firstlogistics.hubservice.hubManager.presentation;

import com.firstlogistics.hubservice.hubManager.application.HubManagerQueryService;
import com.firstlogistics.hubservice.hubManager.presentation.dto.request.SearchHubManagersRequest;
import com.firstlogistics.hubservice.hubManager.presentation.dto.response.HubManagerPageResponse;
import com.firstlogistics.hubservice.hubManager.presentation.dto.response.HubManagerResponse;
import common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/hub-managers")
public class HubManagerApiController {
    private final HubManagerQueryService queryService;


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HubManagerResponse>> getHubManager(@PathVariable UUID id){
        HubManagerResponse response = HubManagerResponse.from(queryService.getHubManager(id));
        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_RETRIEVED,response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<HubManagerPageResponse>> searchHubManagers(@ModelAttribute SearchHubManagersRequest request, @PageableDefault Pageable pageable){
        HubManagerPageResponse response = HubManagerPageResponse.from(queryService.searchHubManagers(request.toQuery(), pageable));
        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_LIST_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_LIST_RETRIEVED,response));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<UUID>> getHubIdByUserId(@PathVariable UUID id){
        UUID hubId = queryService.getHubIdByUserId(id);
        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_HUB_ID_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_HUB_ID_RETRIEVED,hubId));
    }

}
