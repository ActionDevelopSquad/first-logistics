package com.firstlogistics.companyservice.presentation;

import com.firstlogistics.companyservice.application.CompanyCommandService;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.presentation.dto.request.CreateCompanyRequest;
import com.firstlogistics.companyservice.presentation.dto.response.CreateCompanyResponse;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyCommandService companyCommandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<CreateCompanyResponse>> register(@Valid @RequestBody CreateCompanyRequest request) {
        CreateCompanyResponse response = CreateCompanyResponse.from(companyCommandService.register(request.toCommand()));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.CREATED, response));
    }
}
