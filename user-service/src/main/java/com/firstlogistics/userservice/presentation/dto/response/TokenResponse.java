package com.firstlogistics.userservice.presentation.dto.response;

import com.firstlogistics.userservice.application.dto.result.TokenResult;

public record TokenResponse(
        String accessToken,
        int expiresIn,
        String refreshToken,
        int refreshExpiresIn,
        String tokenType
)
{
    public static TokenResponse from(TokenResult result) {
        return new TokenResponse(
                result.accessToken(),
                result.expiresIn(),
                result.refreshToken(),
                result.refreshExpiresIn(),
                result.tokenType()
        );
    }
}