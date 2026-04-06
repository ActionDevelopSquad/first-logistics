package com.firstlogistics.hubservice.hubManager.presentation;

import com.firstlogistics.hubservice.hubManager.application.HubManagerCommandService;
import com.firstlogistics.hubservice.hubManager.application.HubManagerQueryService;
import com.firstlogistics.hubservice.hubManager.presentation.dto.request.SearchHubManagersRequest;
import com.firstlogistics.hubservice.hubManager.presentation.dto.request.UpdateHubManagerRequest;
import com.firstlogistics.hubservice.hubManager.presentation.dto.response.HubManagerPageResponse;
import com.firstlogistics.hubservice.hubManager.presentation.dto.response.HubManagerResponse;
import common.response.ApiResponse;
import common.security.aop.OnlyMaster;
import common.security.aop.RequireRole;
import common.security.domain.CustomUserDetails;
import common.security.entity.enums.UserRole;
import common.security.util.SecurityUtils;
import jakarta.validation.Valid;
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
    private static final UUID SYSTEM_UUID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    private final HubManagerQueryService queryService;
    private final HubManagerCommandService commandService;


    @RequireRole({UserRole.HUB_MANAGER, UserRole.MASTER})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HubManagerResponse>> getHubManager(@PathVariable UUID id, @RequestHeader(value = "X-Forward-Service-Code", required = false) String serviceCode){
        HubManagerResponse response;

        if (isInternalRequest(serviceCode)) {
            response = HubManagerResponse.from(queryService.getHubManager(id));
        } else {
            CustomUserDetails user = SecurityUtils.currentUser();
            response = HubManagerResponse.from(
                    queryService.getHubManager(id, user.getUserId(), user.getRole())
            );
        }

        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_RETRIEVED,response));
    }

    @OnlyMaster
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<HubManagerPageResponse>> searchHubManagers(@Valid @RequestBody SearchHubManagersRequest request, @PageableDefault Pageable pageable){
        HubManagerPageResponse response = HubManagerPageResponse.from(queryService.searchHubManagers(request.toQuery(), pageable));
        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_LIST_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_LIST_RETRIEVED,response));
    }

    @OnlyMaster
    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse<HubManagerResponse>> getHubManagerByUserId(@PathVariable UUID id){
        HubManagerResponse response = HubManagerResponse.from(queryService.getHubManagerByUserId(id));
        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_RETRIEVED,response));
    }

    @OnlyMaster
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<HubManagerResponse>> updateHubManager(@PathVariable UUID id, @Valid @RequestBody UpdateHubManagerRequest request){
        HubManagerResponse response = HubManagerResponse.from(commandService.updateHubManager(id, request.toCommand()));
        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_UPDATED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_UPDATED,response));
    }

    @OnlyMaster
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteHubManager(@PathVariable UUID id, @RequestHeader(value = "X-Forward-Service-Code", required = false) String serviceCode){
        UUID callerId = resolveCallerId(serviceCode);
        commandService.deleteHubManager(id, callerId);
        return ResponseEntity.status(HubManagerSuccessCode.HUB_MANAGER_DELETED.getStatus())
                .body(ApiResponse.success(HubManagerSuccessCode.HUB_MANAGER_DELETED,null));
    }

    private boolean isInternalRequest(String serviceCode) {
        return serviceCode != null && !serviceCode.isBlank();
    }

    private UUID resolveCallerId(String serviceCode) {
        return isInternalRequest(serviceCode)
                ? SYSTEM_UUID
                : SecurityUtils.currentUser().getUserId();
    }

}
