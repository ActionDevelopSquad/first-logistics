package com.firstlogistics.userservice.infrastructure.keycloak.service;

import com.firstlogistics.userservice.application.dto.result.TokenInfo;
import com.firstlogistics.userservice.application.port.KeycloakTokenService;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.infrastructure.keycloak.KeycloakProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakTokenServiceImpl implements KeycloakTokenService {

    private final KeycloakProperties properties;

    @Override
    public TokenInfo generate(String username, String password) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("username", username);
        form.add("password", password);
        form.add("scope", "openid profile email");

        try {
            return RestClient.create()
                    .post()
                    .uri("%s/realms/%s/protocol/openid-connect/token"
                            .formatted(properties.getServerUrl(), properties.getRealm()))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .body(TokenInfo.class);
        } catch (Unauthorized | BadRequest e) {
            throw new UserException(UserErrorCode.ID_PASSWORD_NOT_MATCH);

        } catch (HttpClientErrorException e) {
            throw new UserException(UserErrorCode.AUTH_SERVER_REQUEST_ERROR);

        } catch (HttpServerErrorException e) {
            throw new UserException(UserErrorCode.AUTH_SERVER_INTERNAL_ERROR);

        } catch (RestClientException e) {
            throw new UserException(UserErrorCode.AUTH_SERVER_CONNECTION_ERROR);
        }
    }

    @Override
    public void logout(String refreshToken) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("refresh_token", refreshToken);

        try {
            RestClient.create()
                    .post()
                    .uri("%s/realms/%s/protocol/openid-connect/logout"
                            .formatted(properties.getServerUrl(), properties.getRealm()))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            throw new UserException(UserErrorCode.AUTH_SERVER_REQUEST_ERROR);

        } catch (HttpServerErrorException e) {
            throw new UserException(UserErrorCode.AUTH_SERVER_INTERNAL_ERROR);

        } catch (ResourceAccessException e) {
            throw new UserException(UserErrorCode.AUTH_SERVER_CONNECTION_ERROR);
        }
    }
}