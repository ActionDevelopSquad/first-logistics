package com.firstlogistics.userservice.application.dto.result;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenInfo(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("expires_in")
        int expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("refresh_expires_in")
        int refreshExpiresIn,

        @JsonProperty("token_type")
        String tokenType
) {}