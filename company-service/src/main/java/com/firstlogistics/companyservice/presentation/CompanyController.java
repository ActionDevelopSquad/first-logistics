package com.firstlogistics.companyservice.presentation;

import com.firstlogistics.companyservice.application.CompanyCommandService;
import com.firstlogistics.companyservice.application.CompanyQueryService;
import com.firstlogistics.companyservice.presentation.dto.request.CreateCompanyRequest;
import com.firstlogistics.companyservice.presentation.dto.request.GetCompaniesRequest;
import com.firstlogistics.companyservice.presentation.dto.response.CompanyPageResponse;
import com.firstlogistics.companyservice.presentation.dto.response.CompanyResponse;
import com.firstlogistics.companyservice.presentation.dto.response.CreateCompanyResponse;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyCommandService companyCommandService;
    private final CompanyQueryService companyQueryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateCompanyResponse>> register(
            @Valid @RequestBody CreateCompanyRequest request) {
        CreateCompanyResponse response = CreateCompanyResponse.from(
                companyCommandService.register(request.toCommand()));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(CommonSuccessCode.CREATED, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CompanyPageResponse>> getCompanies(
            @ModelAttribute GetCompaniesRequest request) {
        CompanyPageResponse response = CompanyPageResponse.from(
                companyQueryService.search(request.toQuery()));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

    @GetMapping("/{companyId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
            @PathVariable UUID companyId) {
        CompanyResponse response = CompanyResponse.from(companyQueryService.getById(companyId));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyByManager(
            @PathVariable UUID managerId) {
        CompanyResponse response = CompanyResponse.from(companyQueryService.getByManagerId(managerId));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }
}
