package com.firstlogistics.userservice.presentation.controller;

import com.firstlogistics.userservice.presentation.dto.request.*;
import com.firstlogistics.userservice.presentation.dto.response.TokenResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserIdResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserListResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserResponse;
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
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@Tag(name = "User API", description = "회원 관련 처리를 담당합니다.")
public interface UserControllerDocs {

    @Operation(
            summary = "[API-USER-001] 로그인",
            description = "사용자 아이디와 비밀번호로 로그인하고 Keycloak 토큰을 발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.LOGIN_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.INVALID_INPUT)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "FORBIDDEN", value = UserErrorDocs.FORBIDDEN),
                                    @ExampleObject(name = "NOT_APPROVED_USER", value = UserErrorDocs.NOT_APPROVED_USER)
                            }
                    )
            )
    })
    ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request);

    @Operation(
            summary = "[API-USER-002] 로그아웃",
            description = "Refresh Token을 사용하여 로그아웃합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "로그아웃 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.LOGOUT_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "리프레시 토큰 누락 또는 잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "REFRESH_TOKEN_NOT_FOUND", value = UserErrorDocs.REFRESH_TOKEN_NOT_FOUND),
                                    @ExampleObject(name = "INVALID_REQUEST", value = UserErrorDocs.INVALID_INPUT)
                            }
                    )
            )
    })
    ResponseEntity<ApiResponse<TokenResponse>> logout(
            @Parameter(description = "리프레시 토큰", required = true)
            @RequestHeader("X-Refresh-Token") String refreshToken
    );

    @Operation(
            summary = "[API-USER-003] 액세스 토큰 재발급",
            description = "Refresh Token을 사용하여 Access Token을 재발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "토큰 재발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.REFRESH_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "리프레시 토큰 오류",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "REFRESH_TOKEN_NOT_FOUND", value = UserErrorDocs.REFRESH_TOKEN_NOT_FOUND)
                            }
                    )
            )
    })
    ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @Parameter(description = "리프레시 토큰", required = true)
            @RequestHeader("X-Refresh-Token") String refreshToken
    );

    @Operation(
            summary = "[API-USER-004] 회원가입",
            description = "신규 회원을 등록합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.SIGNUP_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(name = "INVALID_REQUEST", value = UserErrorDocs.INVALID_INPUT),
                                    @ExampleObject(name = "DUPLICATED_USERNAME", value = UserErrorDocs.DUPLICATED_USERNAME)
                            }
                    )
            )
    })
    ResponseEntity<ApiResponse<UserIdResponse>> signUp(@Valid @RequestBody UserCreateRequest request);

    @Operation(
            summary = "[API-USER-005] 회원 단건 조회",
            description = "특정 회원 정보를 조회합니다. MASTER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.GET_USER_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.FORBIDDEN)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.USER_NOT_FOUND)
                    )
            )
    })
    ResponseEntity<ApiResponse<UserResponse>> getUser(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable("userId") UUID userId
    );

    @Operation(
            summary = "[API-USER-006] 마이페이지 조회",
            description = "로그인한 사용자의 회원 정보를 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "마이페이지 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.GET_MY_PAGE_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.USER_NOT_FOUND)
                    )
            )
    })
    ResponseEntity<ApiResponse<UserResponse>> getMyPage(
            @Parameter(description = "로그인 사용자 ID", required = true)
            @RequestHeader("X-User-Id") UUID userId
    );

    @Operation(
            summary = "[API-USER-007] 회원 목록 조회",
            description = "조건에 맞는 회원 목록을 페이징 조회합니다. MASTER 권한이 필요합니다."
    )
    @Parameters({
            @Parameter(name = "page", description = "페이지 번호 (0부터 시작)", example = "0"),
            @Parameter(name = "size", description = "페이지 크기 (10, 30, 50만 허용)", example = "10"),
            @Parameter(name = "sort", description = "정렬 조건", example = "createdAt,desc")
    })
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.GET_USERS_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.FORBIDDEN)
                    )
            )
    })
    ResponseEntity<ApiResponse<UserListResponse>> getUsers(
            @ParameterObject @ModelAttribute UsersGetRequest request,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "[API-USER-008] 회원 권한 변경",
            description = "회원의 역할(Role)을 변경합니다. MASTER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 권한 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.UPDATE_ROLE_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.FORBIDDEN)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.USER_NOT_FOUND)
                    )
            )
    })
    ResponseEntity<ApiResponse<UserIdResponse>> updateRole(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateRoleRequest request
    );

    @Operation(
            summary = "[API-USER-009] 회원 상태 변경",
            description = "회원의 승인 상태를 변경합니다. MASTER 또는 HUB_MANAGER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 상태 변경 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.UPDATE_STATUS_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.FORBIDDEN)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.USER_NOT_FOUND)
                    )
            )
    })
    ResponseEntity<ApiResponse<UserIdResponse>> updateStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateStatusRequest request,
            @Parameter(description = "로그인 사용자 ID", required = true)
            @RequestHeader("X-User-Id") UUID loginId
    );

    @Operation(
            summary = "[API-USER-010] 회원 정보 수정",
            description = "회원 정보를 수정합니다. MASTER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 정보 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.UPDATE_USER_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.INVALID_INPUT)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.FORBIDDEN)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.USER_NOT_FOUND)
                    )
            )
    })
    ResponseEntity<ApiResponse<UserIdResponse>> update(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    );

    @Operation(
            summary = "[API-USER-011] 회원 삭제",
            description = "회원을 삭제 처리합니다. MASTER 권한이 필요합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "회원 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserSuccessDocs.DELETE_USER_SUCCESS)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.FORBIDDEN)
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "회원을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = UserErrorDocs.USER_NOT_FOUND)
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("userId") UUID userId,
            @Parameter(description = "삭제 요청 사용자 ID", required = true)
            @RequestHeader("X-User-Id") UUID deletedUserId
    );
}