package com.firstlogistics.userservice.application.dto;

public record TokenInfo(
        String access_token,
        int expires_in,
        String refresh_token,
        int refresh_expires_in,
        String token_type
) {}