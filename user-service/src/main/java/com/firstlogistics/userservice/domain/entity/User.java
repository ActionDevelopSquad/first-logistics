package com.firstlogistics.userservice.domain.entity;

import com.firstlogistics.userservice.domain.enums.ManagerType;
import com.firstlogistics.userservice.domain.enums.Status;
import com.firstlogistics.userservice.domain.exception.UserErrorCode;
import com.firstlogistics.userservice.domain.exception.UserException;
import common.security.entity.enums.UserRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    private UUID id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String slackId;
    private Status status;
    private UserRole userRole;
    private UUID hubId;
    private ManagerType managerType;
    private LocalDateTime lastLoginAt;

    public static User create(
            UUID userId,
            String username,
            String name,
            String phone,
            String email,
            String slackId,
            UserRole role,
            UUID hubId,
            ManagerType managerType) {
        return new User(
                userId,
                username,
                name,
                phone,
                email,
                slackId,
                Status.PENDING,
                role,
                hubId,
                managerType,
                null
        );
    }

    public void update(String name, String email, String phone, String slackId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.slackId = slackId;
    }

    public void canLogin() {
        if (this.status != Status.APPROVED) {
            throw new UserException(UserErrorCode.CAN_LOGIN_ONLY_APPROVE);
        }
    }

    public void approve() {
        if (this.status == Status.APPROVED) {
            throw new UserException(UserErrorCode.ALREADY_APPROVE);
        }
        this.status = Status.APPROVED;
    }

    public void reject() {
        if (this.status == Status.REJECTED) {
            throw new UserException(UserErrorCode.ALREADY_REJECTED);
        }
        this.status = Status.REJECTED;
    }

    public boolean canManage(User targetUser) {
        if (this.userRole == UserRole.MASTER) {
            return true;
        }

        if (this.userRole == UserRole.HUB_MANAGER) {
            if (!this.hubId.equals(targetUser.getHubId())) {
                return false;
            }

            return targetUser.getUserRole() == UserRole.COMPANY_MANAGER || targetUser.getUserRole() == UserRole.DELIVERY_MANAGER;
        }

        return false;
    }

    public void rollbackStatus() {
        this.status = Status.PENDING;
    }

    public void deliveryManagerOver() {
        this.status = Status.REJECTED;
    }

    public void updateRole(UserRole role) {
        if (userRole == role) {
            throw new UserException(UserErrorCode.SAME_ROLE_SELECTED);
        }
        this.userRole = role;
    }

    public void recordLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public static User reconstitute(
            UUID id,
            String username,
            String name,
            String phone,
            String email,
            String slackId,
            Status status,
            UserRole userRole,
            UUID hubId,
            ManagerType managerType,
            LocalDateTime lastLoginAt
    ) {
        return new User(
                id,
                username,
                name,
                phone,
                email,
                slackId,
                status,
                userRole,
                hubId,
                managerType,
                lastLoginAt
        );
    }
}
