package com.firstlogistics.userservice.application.port;

import com.firstlogistics.userservice.application.dto.result.TokenInfo;

public interface KeycloakTokenService {
    TokenInfo generate(String username, String password);

    void logout(String refreshToken);
}