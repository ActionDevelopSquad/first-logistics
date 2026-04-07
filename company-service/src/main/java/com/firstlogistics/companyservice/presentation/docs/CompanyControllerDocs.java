package com.firstlogistics.companyservice.presentation.docs;

import com.firstlogistics.companyservice.presentation.dto.request.ChangeManagerIdRequest;
import com.firstlogistics.companyservice.presentation.dto.request.CreateCompanyRequest;
import com.firstlogistics.companyservice.presentation.dto.request.GetCompaniesRequest;
import com.firstlogistics.companyservice.presentation.dto.request.UpdateCompanyRequest;
import com.firstlogistics.companyservice.presentation.dto.response.CompanyPageResponse;
import com.firstlogistics.companyservice.presentation.dto.response.CompanyResponse;
import com.firstlogistics.companyservice.presentation.dto.response.CreateCompanyResponse;
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

@Tag(name = "Company API", description = "업체 관련 처리를 담당합니다.")
public interface CompanyControllerDocs {

    @Operation(
            summary = "[API-COM-001] 업체 등록",
            description = "새 업체를 등록합니다. MASTER 또는 HUB_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "업체 등록 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.REGISTER_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "INVALID_INPUT", value = CompanyErrorDocs.INVALID_INPUT),
                                    @ExampleObject(name = "INVALID_COMPANY_TYPE", value = CompanyErrorDocs.INVALID_COMPANY_TYPE),
                                    @ExampleObject(name = "INVALID_HUB_ID", value = CompanyErrorDocs.INVALID_HUB_ID)
                            })
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "담당자 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.DUPLICATE_MANAGER_ID))
            )
    })
    ResponseEntity<ApiResponse<CreateCompanyResponse>> register(
            @Valid @RequestBody CreateCompanyRequest request
    );

    @Operation(
            summary = "[API-COM-002] 업체 목록 조회",
            description = "조건에 맞는 업체 목록을 페이징 조회합니다."
    )
    @Parameters({
            @Parameter(name = "keyword", description = "업체명 검색 키워드"),
            @Parameter(name = "type", description = "업체 유형 (SUPPLIER / RECEIVER)"),
            @Parameter(name = "hubId", description = "허브 ID"),
            @Parameter(name = "status", description = "업체 상태 (ACTIVE / INACTIVE)"),
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "페이지 크기 (10, 30, 50만 허용)", example = "10"),
            @Parameter(name = "sort", description = "정렬 조건", example = "createdAt,desc")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업체 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.GET_COMPANIES_SUCCESS))
            )
    })
    ResponseEntity<ApiResponse<CompanyPageResponse>> getCompanies(
            @ParameterObject @ModelAttribute GetCompaniesRequest request,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "[API-COM-003] 업체 단건 조회",
            description = "업체 ID로 업체 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업체 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.GET_COMPANY_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<CompanyResponse>> getCompany(
            @Parameter(description = "업체 ID", required = true)
            @PathVariable UUID companyId
    );

    @Operation(
            summary = "[API-COM-004] 업체 정보 수정",
            description = "업체 정보를 수정합니다. MASTER, HUB_MANAGER, COMPANY_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업체 수정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.UPDATE_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.INVALID_INPUT))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @Parameter(description = "업체 ID", required = true)
            @PathVariable UUID companyId,
            @Valid @RequestBody UpdateCompanyRequest request
    );

    @Operation(
            summary = "[API-COM-005] 업체 담당자 변경",
            description = "업체의 담당자를 변경합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "담당자 변경 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.UPDATE_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_NOT_FOUND))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "담당자 중복",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.DUPLICATE_MANAGER_ID))
            )
    })
    ResponseEntity<ApiResponse<CompanyResponse>> changeManagerId(
            @Parameter(description = "업체 ID", required = true)
            @PathVariable UUID companyId,
            @Valid @RequestBody ChangeManagerIdRequest request
    );

    @Operation(
            summary = "[API-COM-006] 업체 비활성화",
            description = "업체를 비활성화 처리합니다. MASTER 또는 HUB_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업체 비활성화 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.DEACTIVATE_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "이미 비활성화 상태",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_ALREADY_INACTIVE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<CompanyResponse>> deactivateCompany(
            @Parameter(description = "업체 ID", required = true)
            @PathVariable UUID companyId
    );

    @Operation(
            summary = "[API-COM-007] 업체 활성화",
            description = "업체를 활성화 처리합니다. MASTER 또는 HUB_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업체 활성화 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.ACTIVATE_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "이미 활성화 상태",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_ALREADY_ACTIVE))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<CompanyResponse>> activateCompany(
            @Parameter(description = "업체 ID", required = true)
            @PathVariable UUID companyId
    );

    @Operation(
            summary = "[API-COM-008] 업체 삭제",
            description = "업체를 삭제 처리합니다. MASTER 또는 HUB_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업체 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.DELETE_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.FORBIDDEN))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<Void>> deleteCompany(
            @Parameter(description = "업체 ID", required = true)
            @PathVariable UUID companyId
    );

    @Operation(
            summary = "[API-COM-009] 담당자별 업체 조회",
            description = "담당자 ID로 해당 담당자가 관리하는 업체를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "업체 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanySuccessDocs.GET_COMPANY_SUCCESS))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "업체를 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = CompanyErrorDocs.COMPANY_NOT_FOUND))
            )
    })
    ResponseEntity<ApiResponse<CompanyResponse>> getCompanyByManager(
            @Parameter(description = "담당자 ID", required = true)
            @PathVariable UUID managerId
    );
}
