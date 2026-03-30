package com.firstlogistics.userservice.presentation.controller;

import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.service.UserService;
import com.firstlogistics.userservice.domain.exception.UserSuccessCode;
import com.firstlogistics.userservice.presentation.dto.request.LoginRequest;
import com.firstlogistics.userservice.presentation.dto.response.TokenResponse;
import common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 로그인 시도 -> keyCloack 토큰 발급
     * POST /api/v1/users/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResult tokenResult = userService.login(new LoginCommand(request.username(), request.password()));
        TokenResponse tokenResponse = new TokenResponse(
                tokenResult.accessToken(),
                tokenResult.expiresIn(),
                tokenResult.refreshToken(),
                tokenResult.refreshExpiresIn(),
                tokenResult.tokenType()
        );

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
}
