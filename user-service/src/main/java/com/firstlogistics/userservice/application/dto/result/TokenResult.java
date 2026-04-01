package com.firstlogistics.userservice.application.dto.result;

public record TokenResult (
        String accessToken,
        int expiresIn,
        String refreshToken,
        int refreshExpiresIn,
        String tokenType
)
{}
