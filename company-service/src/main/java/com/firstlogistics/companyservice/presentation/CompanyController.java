package com.firstlogistics.companyservice.presentation;

import com.firstlogistics.companyservice.application.CompanyCommandService;
import com.firstlogistics.companyservice.application.dto.result.CompanyResult;
import com.firstlogistics.companyservice.presentation.dto.CreateCompanyRequest;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public ApiResponse<CompanyResult> register(@Valid @RequestBody CreateCompanyRequest request) {
        CompanyResult result = companyCommandService.register(request.toCommand());
        return ApiResponse.success(CommonSuccessCode.CREATED, result);
    }
}
