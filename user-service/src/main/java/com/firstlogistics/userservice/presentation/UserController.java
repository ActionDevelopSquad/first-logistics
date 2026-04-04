package com.firstlogistics.userservice.presentation;

import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.dto.result.UserResult;
import com.firstlogistics.userservice.application.service.UserService;
import com.firstlogistics.userservice.presentation.dto.request.*;
import com.firstlogistics.userservice.presentation.dto.response.TokenResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserIdResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserListResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserResponse;
import common.response.ApiResponse;
import common.security.aop.OnlyMaster;
import common.security.aop.RequireRole;
import common.security.entity.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인 시도 -> keyCloak 토큰 발급
     * POST /api/v1/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResult tokenResult = userService.login(new LoginCommand(request.username().trim(), request.password()));

        TokenResponse tokenResponse = TokenResponse.from(tokenResult);

        return ResponseEntity
                .status(UserSuccessCode.LOGIN_SUCCESS.getStatus())
                .body(ApiResponse.success(UserSuccessCode.LOGIN_SUCCESS, tokenResponse));
    }

    /**
     * 로그아웃
     * POST /api/v1/users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<TokenResponse>> logout(@RequestHeader("X-Refresh-Token") String refreshToken) {
        userService.logout(refreshToken);

        return ResponseEntity
                .status(UserSuccessCode.LOGOUT_SUCCESS.getStatus())
                .body(ApiResponse.success(UserSuccessCode.LOGOUT_SUCCESS, null));
    }

    /**
     * 엑세스 토큰 재발급
     * POST /api/v1/users/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        TokenResult refresh = userService.refresh(refreshToken);

        TokenResponse tokenResponse = TokenResponse.from(refresh);

        return ResponseEntity
                .status(UserSuccessCode.TOKEN_REFRESHED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.TOKEN_REFRESHED, tokenResponse));
    }

    /**
     * 회원가입
     * POST /api/v1/users/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserIdResponse>> signUp(@Valid @RequestBody UserCreateRequest request) {
        UserCreateCommand userCreateCommand = request.toCommand();

        UUID userId = userService.signup(userCreateCommand);

        return ResponseEntity
                .status(UserSuccessCode.SIGNUP_SUCCESS.getStatus())
                .body(ApiResponse.success(UserSuccessCode.SIGNUP_SUCCESS, new UserIdResponse(userId)));
    }

    /**
     * 회원 단일 조회
     * GET /api/v1/users/{userId}
     * Role : MASTER
     */
    @OnlyMaster
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable("userId") UUID userId) {
        UserResponse response = UserResponse.from(userService.getUser(userId));

        return ResponseEntity
                .status(UserSuccessCode.GET_USER.getStatus())
                .body(ApiResponse.success(UserSuccessCode.GET_USER, response));
    }

    /**
     * 회원 마이페이지 조회
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyPage(@RequestHeader("X-User-Id") UUID userId) {
        UserResponse response = UserResponse.from(userService.getMyPage(userId));

        return ResponseEntity
                .status(UserSuccessCode.GET_USER.getStatus())
                .body(ApiResponse.success(UserSuccessCode.GET_USER, response));
    }

    /**
     * 회원 목록 조회
     * GET /api/v1/users
     * Role : MASTER
     */
    @OnlyMaster
    @GetMapping
    public ResponseEntity<ApiResponse<UserListResponse>> getUsers(@ModelAttribute UsersGetRequest request, Pageable pageable) {
        Page<UserResult> result = userService.getUsers(request.toQuery(), pageable);

        UserListResponse response = UserListResponse.from(result);

        return ResponseEntity
                .status(UserSuccessCode.GET_USERS.getStatus())
                .body(ApiResponse.success(UserSuccessCode.GET_USERS, response));
    }

    /**
     * 회원 권한 변경
     * PATCH /api/v1/users/{userId}/role
     * Role : MASTER
     */
    @OnlyMaster
    @PatchMapping("/{userId}/role")
    public ResponseEntity<ApiResponse<UserIdResponse>> updateRole(@PathVariable("userId") UUID userId, @RequestBody UpdateRoleRequest request) {
        userService.updateRole(userId, request.role());

        return ResponseEntity
                .status(UserSuccessCode.ROLE_UPDATED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.ROLE_UPDATED, new UserIdResponse(userId)));
    }

    /**
     * 회원 승인 여부
     * PATCH /api/v1/users/{userId}/status
     * Role : MASTER, HUB_MANAGER
     */
    @RequireRole({UserRole.MASTER, UserRole.HUB_MANAGER})
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserIdResponse>> updateStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody UpdateStatusRequest request,
            @RequestHeader("X-User-Id") UUID loginId
    )
    {
        userService.updateStatus(userId, request.status(), loginId);

        return ResponseEntity
                .status(UserSuccessCode.STATUS_UPDATED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.STATUS_UPDATED, new UserIdResponse(userId)));
    }

    /**
     * 회원 정보 수정
     * PUT /api/v1/users/{userId}
     * Role : MASTER
     */
    @OnlyMaster
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserIdResponse>> update(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        userService.update(request.toCommand(userId));

        return ResponseEntity
                .status(UserSuccessCode.USER_UPDATED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.USER_UPDATED, new UserIdResponse(userId)));
    }

    /**
     * 회원 탈퇴
     * DELETE /api/v1/users/{userId}
     * Role : MASTER
     */
    @OnlyMaster
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable("userId") UUID userId,
            @RequestHeader("X-User-Id") UUID deletedUserId
    ) {
        userService.delete(userId, deletedUserId);

        return ResponseEntity
                .status(UserSuccessCode.USER_DELETED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.USER_DELETED, null));
    }

}
