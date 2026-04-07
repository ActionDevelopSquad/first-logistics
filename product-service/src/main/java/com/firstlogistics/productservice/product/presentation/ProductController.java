package com.firstlogistics.productservice.product.presentation;

import com.firstlogistics.productservice.product.application.ProductCommandService;
import com.firstlogistics.productservice.product.application.ProductQueryService;
import com.firstlogistics.productservice.product.application.ProductCommandFacade;
import com.firstlogistics.productservice.product.presentation.dto.request.CreateProductRequest;
import com.firstlogistics.productservice.product.presentation.dto.request.GetProductsRequest;
import com.firstlogistics.productservice.product.presentation.dto.request.ChangeProductStatusRequest;
import com.firstlogistics.productservice.product.presentation.dto.request.UpdateProductRequest;
import com.firstlogistics.productservice.product.presentation.dto.response.CreateProductResponse;
import com.firstlogistics.productservice.product.presentation.dto.response.ProductPageResponse;
import com.firstlogistics.productservice.product.presentation.dto.response.ProductResponse;
import com.firstlogistics.productservice.product.presentation.dto.response.StockResponse;
import common.response.ApiResponse;
import common.response.CommonSuccessCode;
import common.security.entity.enums.UserRole;
import common.security.aop.RequireRole;
import common.security.domain.CustomUserDetails;
import common.security.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductCommandService productCommandService;
    private final ProductQueryService productQueryService;
    private final ProductCommandFacade productCommandFacade;

    @RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.COMPANY_MANAGER})
    @PostMapping
    public ResponseEntity<ApiResponse<CreateProductResponse>> register(
            @Valid @RequestBody CreateProductRequest request) {
        CustomUserDetails currentUser = SecurityUtils.currentUser();

        CreateProductResponse response = CreateProductResponse.from(productCommandFacade.register(request.toCommand(currentUser.getUserId(), currentUser.getRole().name())));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(CommonSuccessCode.CREATED, response));
    }

    @RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.COMPANY_MANAGER})
    @PatchMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request) {
        CustomUserDetails currentUser = SecurityUtils.currentUser();
        ProductResponse response = ProductResponse.from(
                productCommandService.update(
                        request.toCommand(currentUser.getUserId(), currentUser.getRole().name(), productId)));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

    @RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.COMPANY_MANAGER})
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> changeProductStatus(
            @PathVariable UUID productId,
            @Valid @RequestBody ChangeProductStatusRequest request) {
        CustomUserDetails currentUser = SecurityUtils.currentUser();
        ProductResponse response = ProductResponse.from(
                productCommandService.changeStatus(
                        request.toCommand(currentUser.getUserId(), currentUser.getRole().name(), productId)));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

    @RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER, UserRole.COMPANY_MANAGER})
    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable UUID productId) {
        CustomUserDetails currentUser = SecurityUtils.currentUser();
        productCommandService.delete(productId, currentUser.getUserId(), currentUser.getRole().name());
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, null));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @PathVariable UUID productId) {
        ProductResponse response = ProductResponse.from(productQueryService.getById(productId));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

    @GetMapping("/{productId}/stock")
    public ResponseEntity<ApiResponse<StockResponse>> getStock(
            @PathVariable UUID productId) {
        StockResponse response = StockResponse.from(productQueryService.getStock(productId));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(
            @ModelAttribute GetProductsRequest request,
            Pageable pageable) {
        ProductPageResponse response = ProductPageResponse.from(
                productQueryService.search(request.toQuery(), pageable));
        return ResponseEntity.ok(ApiResponse.success(CommonSuccessCode.OK, response));
    }
}
