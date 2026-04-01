package com.firstlogistics.hubservice.hub.presentation;

import com.firstlogistics.hubservice.hub.application.HubCommandService;
import com.firstlogistics.hubservice.hub.application.HubQueryService;
import com.firstlogistics.hubservice.hub.application.dto.result.SearchHubResult;
import com.firstlogistics.hubservice.hub.presentation.dto.request.CreateHubRequest;
import com.firstlogistics.hubservice.hub.presentation.dto.request.SearchHubsRequest;
import com.firstlogistics.hubservice.hub.presentation.dto.response.HubDetailResponse;
import com.firstlogistics.hubservice.hub.presentation.dto.response.HubPageResponse;
import com.firstlogistics.hubservice.hub.presentation.dto.response.HubResponse;
import com.firstlogistics.hubservice.hub.presentation.dto.response.NearestHubResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubApiController {

    private final HubCommandService hubCommandService;
    private final HubQueryService hubQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<HubResponse>> create(@Valid @RequestBody CreateHubRequest request){
        HubResponse response = HubResponse.from(hubCommandService.create(request.toCommand()));
        return ResponseEntity.status(HubSuccessCode.HUB_CREATED.getStatus())
                .body(ApiResponse.success(
                        HubSuccessCode.HUB_CREATED, response
                ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<HubDetailResponse>> get(@PathVariable UUID id){
        HubDetailResponse response = HubDetailResponse.from(hubQueryService.getHub(id));
        return ResponseEntity.status(HubSuccessCode.HUB_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubSuccessCode.HUB_RETRIEVED, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<HubPageResponse>> search(
            @Valid @ModelAttribute SearchHubsRequest request,
            @PageableDefault Pageable pageable
            ){
        HubPageResponse response = HubPageResponse.from(hubQueryService.searchHubs(request.toQuery(),pageable));
        return ResponseEntity.status(HubSuccessCode.HUB_LIST_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubSuccessCode.HUB_LIST_RETRIEVED, response));
    }

    @GetMapping("/nearest")
    public ResponseEntity<ApiResponse<NearestHubResponse>> getNearest(
            @RequestParam
            @DecimalMin("-90.0")
            @DecimalMax("90.0")
            Double latitude,

            @RequestParam
            @DecimalMin("-180.0")
            @DecimalMax("180.0")
            Double longitude
    ){
        UUID response = hubQueryService.getNearestHub(latitude, longitude);
        return ResponseEntity.status(HubSuccessCode.HUB_RETRIEVED.getStatus())
                .body(ApiResponse.success(HubSuccessCode.HUB_RETRIEVED, new NearestHubResponse(response)));
    }
}
