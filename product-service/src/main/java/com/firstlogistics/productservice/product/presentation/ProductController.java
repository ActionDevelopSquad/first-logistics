package com.firstlogistics.productservice.product.presentation;

import com.firstlogistics.productservice.product.application.ProductCommandService;
import com.firstlogistics.productservice.product.presentation.dto.request.CreateProductRequest;
import com.firstlogistics.productservice.product.presentation.dto.response.CreateProductResponse;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import common.security.entity.enums.UserRole;
import common.security.security.aop.RequireRole;
import common.security.security.domain.CustomUserDetails;
import common.security.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;

    @RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.COMPANY_MANAGER})
    @PostMapping
    public ResponseEntity<ApiResponse<CreateProductResponse>> register(@Valid @RequestBody CreateProductRequest request) {
        CustomUserDetails currentUser = SecurityUtils.currentUser();
        CreateProductResponse response = CreateProductResponse.from(productCommandService.register(request.toCommand(currentUser.getUserId(), currentUser.getRole().name())));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(CommonSuccessCode.CREATED, response));
    }
}
