package com.firstlogistics.hubservice.hubconnection.presentation;

import com.firstlogistics.hubservice.hubconnection.application.HubConnectionCommandService;
import com.firstlogistics.hubservice.hubconnection.application.HubConnectionQueryService;
import com.firstlogistics.hubservice.hubconnection.application.HubRouteQueryService;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.request.ChangeHubConnectionStatusRequest;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.request.CreateHubConnectionRequest;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.request.SearchHubConnectionRequest;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.request.UpdateHubConnectionRequest;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.response.HubConnectionPageResponse;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.response.HubConnectionResponse;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.response.HubRouteResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hub-connections")
@RequiredArgsConstructor
public class HubConnectionApiController {

    private final HubConnectionCommandService commandService;
    private final HubRouteQueryService hubRouteQueryService;
    private final HubConnectionQueryService hubConnectionQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<HubConnectionResponse>> create(@Valid @RequestBody CreateHubConnectionRequest request){
        HubConnectionResponse response = HubConnectionResponse.from(commandService.create(request.toCommand()));
        return ResponseEntity.status(HubConnectionSuccessCode.HUB_CONNECTION_CREATED.getStatus())
                .body(ApiResponse.success(HubConnectionSuccessCode.HUB_CONNECTION_CREATED,response));
    }

    @GetMapping("/routes")
    public ResponseEntity<ApiResponse<HubRouteResponse>> getRoutes(@RequestParam UUID sourceHubId,
                                                                   @RequestParam UUID destinationHubId,
                                                                   @RequestParam UUID destinationCompanyId,
                                                                   @RequestParam(required = false) String policy
    ){
        HubRouteResponse response = HubRouteResponse.from(hubRouteQueryService.getRoute(sourceHubId,destinationCompanyId,policy));

        return ResponseEntity.status(HubConnectionSuccessCode.HUB_ROUTES_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubConnectionSuccessCode.HUB_ROUTES_RETRIEVED, response));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HubConnectionResponse>> getHubConnection(@PathVariable UUID id){
        HubConnectionResponse response = HubConnectionResponse.from(hubConnectionQueryService.getHubConnection(id));
        return ResponseEntity.status(HubConnectionSuccessCode.HUB_CONNECTION_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubConnectionSuccessCode.HUB_CONNECTION_RETRIEVED, response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<HubConnectionPageResponse>> searchHubConnection(@ModelAttribute SearchHubConnectionRequest request, @PageableDefault Pageable pageable){
        HubConnectionPageResponse response = HubConnectionPageResponse.from(hubConnectionQueryService.searchHubConnection(request.toQuery(),pageable));
        return ResponseEntity.status(HubConnectionSuccessCode.HUB_CONNECTION_LIST_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubConnectionSuccessCode.HUB_CONNECTION_LIST_RETRIEVED, response));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<HubConnectionResponse>> update(@PathVariable UUID id, @Valid @RequestBody UpdateHubConnectionRequest request) {
        HubConnectionResponse response = HubConnectionResponse.from(commandService.update(id, request.toCommand()));
        return ResponseEntity.status(HubConnectionSuccessCode.HUB_CONNECTION_UPDATED.getStatus())
                .body(ApiResponse.success(
                        HubConnectionSuccessCode.HUB_CONNECTION_UPDATED, response));

    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<HubConnectionResponse>> changeStatus(@PathVariable UUID id, @Valid @RequestBody ChangeHubConnectionStatusRequest request) {
        HubConnectionResponse response = HubConnectionResponse.from(commandService.changeStatus(id, request.toCommand()));
        return ResponseEntity.status(HubConnectionSuccessCode.HUB_CONNECTION_UPDATED.getStatus())
                .body(ApiResponse.success(
                        HubConnectionSuccessCode.HUB_CONNECTION_UPDATED, response));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id){
        commandService.delete(id);
        return ResponseEntity.status(HubConnectionSuccessCode.HUB_CONNECTION_DELETED.getStatus())
                .body(ApiResponse.success(HubConnectionSuccessCode.HUB_CONNECTION_DELETED,null));
    }
}
