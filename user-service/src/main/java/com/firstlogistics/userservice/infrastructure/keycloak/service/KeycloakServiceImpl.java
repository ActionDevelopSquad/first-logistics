package com.firstlogistics.userservice.infrastructure.keycloak.service;

import com.firstlogistics.userservice.application.dto.command.UserCreateCommand;
import com.firstlogistics.userservice.application.dto.command.UserUpdateCommand;
import com.firstlogistics.userservice.application.port.KeycloakService;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import com.firstlogistics.userservice.infrastructure.keycloak.KeycloakProperties;
import common.jpa.entity.enums.UserRole;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties(KeycloakProperties.class)
public class KeycloakServiceImpl implements KeycloakService {

    private final KeycloakProperties properties;
    private final Keycloak keycloak;

    @Override
    public UUID signup(UserCreateCommand command) {
        UsersResource usersResource = keycloak.realm(properties.getRealm()).users();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setUsername(command.username());
        user.setFirstName(command.firstName());
        user.setLastName(command.lastName());
        user.setEmail(command.email());

        // 커스텀 attributes
        Map<String, List<String>> attributes = new HashMap<>();
        attributes.put("phone", List.of(command.phone()));
        attributes.put("slackId", List.of(command.slackId()));
        user.setAttributes(attributes);

        // 사용자 생성 요청
        Response response = usersResource.create(user);

        // 사용자 생성 성공 여부
        if (response.getStatus() != 201) {

            String errorBody = "";
            try {
                errorBody = response.readEntity(String.class);
            } catch (Exception ignored) {}

            if (errorBody.contains("same username")) {
                throw new UserException(UserErrorCode.DUPLICATED_USERNAME);
            }

            if (errorBody.contains("same email")) {
                throw new UserException(UserErrorCode.DUPLICATED_EMAIL);
            }

            throw new HttpClientErrorException(
                    HttpStatus.valueOf(response.getStatus()),
                    errorBody
            );
        }

        // 생성된 사용자 ID 추출
        String userId = CreatedResponseUtil.getCreatedId(response);

        // 비밀번호 객체 생성
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(command.password());

        // 생성한 사용자 비밀번호 설정
        usersResource.get(userId).resetPassword(passwordCred);

        // realm role 조회 & 추가
        RoleRepresentation userRole = keycloak.realm(properties.getRealm()).roles().get(command.userRole().name()).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(List.of(userRole));

        return UUID.fromString(userId);
    }

    @Override
    public void updateRole(UUID userId, List<UserRole> roles) {
        String id = userId.toString();
        String realm = properties.getRealm();
        RoleScopeResource resource = keycloak.realm(realm).users().get(id).roles().realmLevel();

        resource.remove(resource.listAll());

        List<RoleRepresentation> newRoles = roles.stream()
                .map(roleName -> keycloak.realm(realm).roles().get(roleName.name()).toRepresentation())
                .toList();
        resource.add(newRoles);
    }

    @Override
    public void updateUser(UserUpdateCommand command) {
        UserRepresentation user = getUserProfile(command.userId());

        if (StringUtils.hasText(command.firstName())) {
            user.setFirstName(command.firstName());
        }

        if (StringUtils.hasText(command.lastName())) {
            user.setLastName(command.lastName());
        }

        if (StringUtils.hasText(command.email())) {
            user.setEmail(command.email());
        }

        Map<String, List<String>> attributes = Objects.requireNonNullElseGet(user.getAttributes(), HashMap::new);

        if (StringUtils.hasText(command.phone())) {
            attributes.put("phone", List.of(command.phone()));
            attributes.put("slackId", List.of(command.slackId()));
        }

        user.setAttributes(attributes);

        keycloak.realm(properties.getRealm()).users().get(command.userId().toString()).update(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        try {
            keycloak.realm(properties.getRealm())
                    .users()
                    .get(userId.toString())
                    .remove();
        } catch (NotFoundException e) {
            // 이미 없으면 삭제된 것으로 간주

        } catch (Exception e) {
            throw new UserException(UserErrorCode.AUTH_SERVER_INTERNAL_ERROR);
        }
    }

    private UserRepresentation getUserProfile(UUID userId) {
        return keycloak.realm(properties.getRealm()).users().get(userId.toString()).toRepresentation();
    }
}
