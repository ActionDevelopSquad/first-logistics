package com.firstlogistics.hubservice.hub.presentation;

import com.firstlogistics.hubservice.hub.application.HubCommandService;
import com.firstlogistics.hubservice.hub.presentation.request.dto.CreateHubRequest;
import com.firstlogistics.hubservice.hub.presentation.response.code.HubSuccessCode;
import com.firstlogistics.hubservice.hub.presentation.response.dto.HubResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hubs")
@RequiredArgsConstructor
public class HubApiController {

    private final HubCommandService hubCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<HubResponse>> create(@Valid @RequestBody CreateHubRequest request){
        HubResponse response = HubResponse.from(hubCommandService.create(request.toCommand()));
        return ResponseEntity.status(HubSuccessCode.HUB_CREATED.getStatus())
                .body(ApiResponse.success(
                        HubSuccessCode.HUB_CREATED, response
                ));
    }
}
