package com.firstlogistics.hubservice.hubconnection.presentation;

import com.firstlogistics.hubservice.hubconnection.application.HubConnectionCommandService;
import com.firstlogistics.hubservice.hubconnection.application.HubRouteQueryService;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.request.CreateHubConnectionRequest;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.response.HubConnectionResponse;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.response.HubRouteResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hub-connections")
@RequiredArgsConstructor
public class HubConnectionApiController {

    private final HubConnectionCommandService commandService;
    private final HubRouteQueryService hubRouteQueryService;

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
        HubRouteResponse response;
        if(policy == null || policy.isBlank())
            response = HubRouteResponse.from(hubRouteQueryService.getRoute(sourceHubId,destinationHubId,destinationCompanyId));
        else response = HubRouteResponse.from(hubRouteQueryService.getRoute(sourceHubId,destinationHubId,destinationCompanyId,policy));

        return ResponseEntity.status(HubConnectionSuccessCode.HUB_ROUTES_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubConnectionSuccessCode.HUB_ROUTES_RETRIEVED, response));

    }
}
