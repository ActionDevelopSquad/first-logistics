package com.firstlogistics.userservice.presentation.dto.response;

public record TokenResponse(
        String accessToken,
        int expiresIn,
        String refreshToken,
        int refreshExpiresIn,
        String tokenType
) {}