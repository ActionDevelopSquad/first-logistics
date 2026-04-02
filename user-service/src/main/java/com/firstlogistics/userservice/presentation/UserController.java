package com.firstlogistics.userservice.presentation;

import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import com.firstlogistics.userservice.application.dto.query.UserGetQuery;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.dto.result.UserResult;
import com.firstlogistics.userservice.application.service.UserService;
import com.firstlogistics.userservice.presentation.dto.request.*;
import com.firstlogistics.userservice.presentation.dto.response.TokenResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserIdResponse;
import com.firstlogistics.userservice.presentation.dto.response.UserResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
        TokenResult tokenResult = userService.login(new LoginCommand(request.username().trim(), request.password().trim()));

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
     * 회원 권한 변경
     * PATCH /api/v1/users/{userId}/role
     * Role : MASTER
     */
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
    @PatchMapping("/{userId}/status")
    public ResponseEntity<ApiResponse<UserIdResponse>> updateStatus(@PathVariable("userId") UUID userId, @RequestBody UpdateStatusRequest request) {
        userService.updateStatus(userId, request.status());

        return ResponseEntity
                .status(UserSuccessCode.STATUS_UPDATED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.STATUS_UPDATED, new UserIdResponse(userId)));
    }

    /**
     * 회원 정보 수정
     * PUT /api/v1/users/{userId}
     * Role : MASTER
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserIdResponse>> update(
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserUpdateCommand command = new UserUpdateCommand(
                userId,
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone(),
                request.slackId()
        );

        userService.update(command);

        return ResponseEntity
                .status(UserSuccessCode.USER_UPDATED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.USER_UPDATED, new UserIdResponse(userId)));
    }

    /**
     * 회원 탈퇴
     * DELETE /api/v1/users/{userId}
     * Role : MASTER
     */
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

    /**
     * 엑세스 토큰 재발급
     * POST /api/v1/users/refresh
     */
    @GetMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestHeader("X-Refresh-Token") String refreshToken) {
        TokenResult refresh = userService.refresh(refreshToken);

        TokenResponse tokenResponse = TokenResponse.from(refresh);

        return ResponseEntity
                .status(UserSuccessCode.TOKEN_REFRESHED.getStatus())
                .body(ApiResponse.success(UserSuccessCode.TOKEN_REFRESHED, tokenResponse));
    }

    /**
     * 회원 단일 조회
     * GET /api/v1/users/{userId}
     * Role : MASTER
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable("userId") UUID userId) {
        UserResult result = userService.getUser(userId);

        UserResponse response = UserResponse.from(result);

        return ResponseEntity
                .status(UserSuccessCode.GET_USER.getStatus())
                .body(ApiResponse.success(UserSuccessCode.GET_USER, response));
    }

    /**
     * 회원 마이페이지 조회
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyPage(@RequestHeader("X-User-Id") String userId) {
        UserResult result = userService.getMyPage(UUID.fromString(userId));

        UserResponse response = UserResponse.from(result);

        return ResponseEntity
                .status(UserSuccessCode.GET_USERS.getStatus())
                .body(ApiResponse.success(UserSuccessCode.GET_USERS, response));
    }

    /**
     * 회원 목록 조회
     * GET /api/v1/users
     * Role : MASTER
     */
    @GetMapping()
    public ResponseEntity<ApiResponse<UserResponse>> getUsers(@ModelAttribute UsersGetRequest request) {
        UserGetQuery query = request.toQuery();
        UserResult result = userService.getUsers(query);

        UserResponse response = UserResponse.from(result);

        return ResponseEntity
                .status(UserSuccessCode.GET_USERS.getStatus())
                .body(ApiResponse.success(UserSuccessCode.GET_USERS, response));
    }

}
