package com.firstlogistics.userservice.application.port;

import com.firstlogistics.userservice.application.dto.TokenInfo;

public interface KeycloackTokenService {
    TokenInfo generate(String username, String password);

    void logout(String refreshToken);
}