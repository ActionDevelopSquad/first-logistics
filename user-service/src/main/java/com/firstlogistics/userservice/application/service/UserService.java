package com.firstlogistics.userservice.application.service;

import com.firstlogistics.userservice.application.dto.TokenInfo;
import com.firstlogistics.userservice.application.dto.command.LoginCommand;
import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.result.TokenResult;
import com.firstlogistics.userservice.application.port.KeycloackService;
import com.firstlogistics.userservice.application.port.KeycloackTokenService;
import com.firstlogistics.userservice.domain.entity.User;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final KeycloackTokenService tokenService;
    private final UserRepository userRepository;

    @Transactional
    public TokenResult login(LoginCommand command) {
        User user = userRepository.findByUsernameNotDeleted(command.username());

        user.canLogin();

        TokenInfo tokenInfo = tokenService.generate(command.username(), command.password());

        user.recordLogin();
        userRepository.save(user);

        return new TokenResult(
                tokenInfo.access_token(),
                tokenInfo.expires_in(),
                tokenInfo.refresh_token(),
                tokenInfo.refresh_expires_in(),
                tokenInfo.token_type()
        );
    }

    public void logout(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new UserException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        tokenService.logout(refreshToken);
    }
}
