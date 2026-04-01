package com.firstlogistics.hubservice.hubconnection.presentation;

import com.firstlogistics.hubservice.hubconnection.application.HubConnectionCommandService;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.request.CreateHubConnectionRequest;
import com.firstlogistics.hubservice.hubconnection.presentation.dto.response.HubConnectionResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hub-connections")
@RequiredArgsConstructor
public class HubConnectionApiController {

    private final HubConnectionCommandService commandService;

    @PostMapping
    public ResponseEntity<ApiResponse<HubConnectionResponse>> create(@Valid @RequestBody CreateHubConnectionRequest request){
        HubConnectionResponse response = HubConnectionResponse.from(commandService.create(request.toCommand()));
        return ResponseEntity.status(HubConnectionSuccessCode.HUB_CREATED.getStatus())
                .body(ApiResponse.success(HubConnectionSuccessCode.HUB_CREATED,response));
    }
}
