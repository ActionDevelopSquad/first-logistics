package com.firstlogistics.productservice.product.presentation.docs;

import com.firstlogistics.productservice.product.presentation.dto.request.ChangeProductStatusRequest;
import com.firstlogistics.productservice.product.presentation.dto.request.CreateProductRequest;
import com.firstlogistics.productservice.product.presentation.dto.request.GetProductsRequest;
import com.firstlogistics.productservice.product.presentation.dto.request.UpdateProductRequest;
import com.firstlogistics.productservice.product.presentation.dto.response.CreateProductResponse;
import com.firstlogistics.productservice.product.presentation.dto.response.ProductPageResponse;
import com.firstlogistics.productservice.product.presentation.dto.response.ProductResponse;
import com.firstlogistics.productservice.product.presentation.dto.response.StockResponse;
import common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Tag(name = "Product API", description = "상품 관련 처리를 담당합니다.")
public interface ProductControllerDocs {

    @Operation(
            summary = "[API-PRO-001] 상품 등록",
            description = "새 상품을 등록합니다. MASTER, HUB_MANAGER, COMPANY_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "상품 등록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductSuccessDocs.REGISTER_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.INVALID_INPUT))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "FORBIDDEN", value = ProductErrorDocs.FORBIDDEN),
                                    @ExampleObject(name = "UNAUTHORIZED_COMPANY_ACCESS", value = ProductErrorDocs.UNAUTHORIZED_COMPANY_ACCESS)
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.COMPANY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<CreateProductResponse>> register(
            @Valid @RequestBody CreateProductRequest request
    );

    @Operation(
            summary = "[API-PRO-002] 상품 정보 수정",
            description = "상품 정보를 수정합니다. MASTER, HUB_MANAGER, COMPANY_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상품 수정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductSuccessDocs.UPDATE_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.INVALID_INPUT))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.PRODUCT_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateProductRequest request
    );

    @Operation(
            summary = "[API-PRO-003] 상품 상태 변경",
            description = "상품의 판매 상태를 변경합니다. MASTER, HUB_MANAGER, COMPANY_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상품 상태 변경 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductSuccessDocs.CHANGE_STATUS_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 상태값",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "PRODUCT_ALREADY_STOPPED", value = ProductErrorDocs.PRODUCT_ALREADY_STOPPED),
                                    @ExampleObject(name = "PRODUCT_ALREADY_SELLING", value = ProductErrorDocs.PRODUCT_ALREADY_SELLING)
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.PRODUCT_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<ProductResponse>> changeProductStatus(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable UUID productId,
            @Valid @RequestBody ChangeProductStatusRequest request
    );

    @Operation(
            summary = "[API-PRO-004] 상품 삭제",
            description = "상품을 삭제 처리합니다. MASTER, HUB_MANAGER, COMPANY_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상품 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductSuccessDocs.DELETE_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.PRODUCT_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable UUID productId
    );

    @Operation(
            summary = "[API-PRO-005] 상품 단건 조회",
            description = "상품 ID로 상품 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상품 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductSuccessDocs.GET_PRODUCT_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "상품을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.PRODUCT_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<ProductResponse>> getProduct(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable UUID productId
    );

    @Operation(
            summary = "[API-PRO-006] 상품 목록 조회",
            description = "조건에 맞는 상품 목록을 페이징 조회합니다."
    )
    @Parameters({
            @Parameter(name = "keyword", description = "상품명 검색 키워드"),
            @Parameter(name = "companyId", description = "업체 ID"),
            @Parameter(name = "hubId", description = "허브 ID"),
            @Parameter(name = "status", description = "상품 상태 (SELLING / STOPPED)"),
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "페이지 크기 (10, 30, 50만 허용)", example = "10"),
            @Parameter(name = "sort", description = "정렬 조건", example = "createdAt,desc")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "상품 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductSuccessDocs.GET_PRODUCTS_SUCCESS))
            )
    })
    ResponseEntity<ApiResponse<ProductPageResponse>> getProducts(
            @ParameterObject @ModelAttribute GetProductsRequest request,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "[API-PRO-007] 상품 재고 조회",
            description = "상품 ID로 현재 재고 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "재고 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductSuccessDocs.GET_STOCK_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "재고 정보를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = ProductErrorDocs.INVENTORY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<StockResponse>> getStock(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable UUID productId
    );
}
